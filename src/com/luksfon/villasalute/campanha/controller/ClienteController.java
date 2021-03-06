package com.luksfon.villasalute.campanha.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;

import com.luksfon.villasalute.campanha.database.DatabaseManager;
import com.luksfon.villasalute.campanha.entity.Cliente;
import com.luksfon.villasalute.campanha.entity.EntityBase;
import com.luksfon.villasalute.campanha.exception.BusinessException;

public class ClienteController<E extends EntityBase> extends DatabaseManager {

	public ClienteController(boolean buildAllEntities, Context context) {
		super(buildAllEntities, context);
	}

	public <T extends EntityBase> int insert(T entity,
			ContentResolver contentResolver) throws IllegalAccessException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException,
			BusinessException {
		int id = -1;
		try {
			beginTransaction();

			id = super.insert(entity);
			addContact((Cliente) entity, contentResolver);

			saveChanges();
		} catch (Exception ex) {
			Log.println(0, "insert", ex.getMessage());
		}
		return id;
	}

	public <T extends EntityBase> int delete(T entity,
			ContentResolver contentResolver) throws BusinessException,
			IllegalAccessException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException {
		beginTransaction();

		int id = super.delete(entity);
		deleteContact((Cliente) entity, contentResolver);
		saveChanges();
		return id;
	}

	public <T extends EntityBase> int edit(T entity,
			ContentResolver contentResolver) throws IllegalAccessException,
			IllegalArgumentException, NoSuchMethodException,
			InvocationTargetException, ClassNotFoundException,
			BusinessException {
		beginTransaction();
		this.delete(entity, contentResolver);
		int id = this.insert(entity, contentResolver);
		saveChanges();
		return id;
	}

	private void addContact(Cliente cliente, ContentResolver contentResolver) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		// ------------------------------------------------------ Names
		if (cliente.getNome() != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(
							ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
							cliente.getNome()).build());
		}

		// ------------------------------------------------------ Mobile Number
		if (cliente.getTelefone() != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
							cliente.getTelefone())
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
					.build());
		}

		// ------------------------------------------------------ Email
		if (cliente.getEmail() != null) {
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(
							ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(
							ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA,
							cliente.getEmail())
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE,
							ContactsContract.CommonDataKinds.Email.TYPE_WORK)
					.build());
		}

		try {
			contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this.context, "Exception: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void deleteContact(Cliente cliente, ContentResolver contentResolver) {
		Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(cliente.getTelefone()));
		Cursor cur = contentResolver.query(contactUri, null, null, null, null);
		try {
			if (cur.moveToFirst()) {
				if (cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME))
						.equalsIgnoreCase(cliente.getNome())) {
					String lookupKey = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
					Uri uri = Uri.withAppendedPath(
							ContactsContract.Contacts.CONTENT_LOOKUP_URI,
							lookupKey);
					contentResolver.delete(uri, null, null);
				}
			}
		} finally {
			cur.close();
		}
	}
}