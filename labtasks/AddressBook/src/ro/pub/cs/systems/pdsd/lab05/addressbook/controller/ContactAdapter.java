package ro.pub.cs.systems.pdsd.lab05.addressbook.controller;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ro.pub.cs.systems.pdsd.lab05.addressbook.R;
import ro.pub.cs.systems.pdsd.lab05.addressbook.general.Constants;
import ro.pub.cs.systems.pdsd.lab05.addressbook.general.Utilities;
import ro.pub.cs.systems.pdsd.lab05.addressbook.model.Contact;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter implements LoaderManager.LoaderCallbacks<Cursor> {
	
	Activity context;
	
	ArrayList<Contact> data;
	String searchCriteria;
	
	Cursor cursor;
	int checkedItemPosition = -1;
	
    private static final String[] PROJECTION = {
    	Contacts._ID,
    	Contacts.LOOKUP_KEY,
    	Contacts.PHOTO_THUMBNAIL_URI,
    	Contacts.DISPLAY_NAME_PRIMARY,
    	Contacts.STARRED,
    	Contacts.TIMES_CONTACTED,
    	Contacts.LAST_TIME_CONTACTED
    };

    private static final int ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;
    private static final int PHOTO_THUMBNAIL_URI_INDEX = 2;
    private static final int DISPLAY_NAME_INDEX = 3;
    private static final int STARRED_INDEX = 4;
    private static final int TIMES_CONTACTED_INDEX = 5;
    private static final int LAST_TIME_CONTACTED_INDEX = 6;
	
    private static final String SELECTION = "(" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
    		+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("
    		+ Contacts.DISPLAY_NAME + "!='')";
    
    public static class ViewHolder {
    	ImageView contactPhotoImageView, contactStarredImageView;
    	TextView contactNameTextView, contactTimesContactedTextView, contactLastTimeContactedTextView;
    };

	public ContactAdapter(Activity context) {
		this.context = context;
		data = new ArrayList<Contact>();
		initData();
	}
	
	public void initData() {
		context.getLoaderManager().initLoader(Constants.BASIC_DETAILS_QUERY, null, this);
	}
	
	public void resetData(String searchCriteria) {
		this.searchCriteria = searchCriteria;
		context.getLoaderManager().restartLoader(Constants.BASIC_DETAILS_QUERY, null, this);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		//TODO: exercise 5b
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		//TODO: exercise 5b
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Contact contact = data.get(position);
		LayoutInflater inflater = (LayoutInflater)context.getLayoutInflater();
		View contactView;
		
		if (position%2==0) {
			contactView = inflater.inflate(R.layout.contact_view_even, parent, false);
		} else {
			contactView = inflater.inflate(R.layout.contact_view_odd, parent, false);
		}
		
		// TODO: exercise 5
		 ImageView contactPhotoImageView = (ImageView)contactView.findViewById(R.id.contact_photo_image_view);
		    TextView contactNameTextView = (TextView)contactView.findViewById(R.id.contact_name_text_view);
		    TextView contactTimesContactedTextView = (TextView)contactView.findViewById(R.id.contact_times_contacted_text_view);
		    TextView contactLastTimeContactedTextView = (TextView)contactView.findViewById(R.id.contact_last_time_contacted_text_view);
		    ImageView contactStarredImageView = (ImageView)contactView.findViewById(R.id.contact_starred_image_view);
		 
		    if (getCheckedItemPosition() == position) {
		      contactView.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
		    } else {
		      contactView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
		    }
		 
		    AssetFileDescriptor assetFileDescriptor = null;
		    try {
		      Uri contactPhotoUri = contact.getPhoto();
		      if (contactPhotoUri == Uri.EMPTY) {
		        contactPhotoImageView.setImageResource(R.drawable.contact_photo);
		      } else {
		        assetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(contactPhotoUri, "r");
			FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
			if (fileDescriptor != null) {
			  contactPhotoImageView.setImageBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor, null, null));
			}
		      }
		    } catch (FileNotFoundException fileNotFoundException) {
		      Log.e(Constants.TAG, "An exception has occurred: "+fileNotFoundException.getMessage());
		      if (Constants.DEBUG) {
			fileNotFoundException.printStackTrace();
		      }
		    } finally {
		      if (assetFileDescriptor != null) {
		        try {
		          assetFileDescriptor.close();
			} catch (IOException ioException) {
			  Log.e(Constants.TAG, "An exception has occurred: "+ioException.getMessage());
		          if (Constants.DEBUG) {
		            ioException.printStackTrace();
		          }
		        }
		      }
		    }
		 
		    contactNameTextView.setText(contact.getName());
		    contactTimesContactedTextView.setText(context.getResources().getString(R.string.times_contacted)+" "+contact.getTimesContacted());
		    long value = contact.getLastTimeContacted();
		    if (value != 0) {
		      contactLastTimeContactedTextView.setText(context.getResources().getString(R.string.last_time_contacted)+" "+Utilities.displayDateAndTime(value));
		    } else {
		      contactLastTimeContactedTextView.setText(context.getResources().getString(R.string.last_time_contacted)+" -");
		    }
		    if (contact.getStarred() == 1) {
		      contactStarredImageView.setImageResource(R.drawable.star_set);
		    } else {
		      contactStarredImageView.setImageResource(R.drawable.star_unset);
		    }
		    return contactView;
		
		// TODO: exercise 7
		
		// TODO: exercise 8 (optional)
		
	}
	
	public final static int CONTACT_VIEW_TYPES     = 2;
	
	@Override
	public int getViewTypeCount() {
	  return CONTACT_VIEW_TYPES;
	}
	 
	@Override
	public int getItemViewType(int position) {
	  return(position % CONTACT_VIEW_TYPES);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri contentUri;
				
		if (searchCriteria != null && !searchCriteria.isEmpty()) {
			contentUri = Uri.withAppendedPath(
					Contacts.CONTENT_FILTER_URI,
					Uri.encode(searchCriteria));
		} else {
			contentUri = Contacts.CONTENT_URI;
		}
				
		return new CursorLoader(context,
				contentUri, 
				PROJECTION, 
				SELECTION, 
				null, 
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		data = new ArrayList<Contact>();
		if (cursor.getCount() != 0) {
			Log.d(Constants.TAG, "Found "+cursor.getCount()+" contacts");
			cursor.moveToFirst();
			do {
				Contact contact = new Contact();
				String photoThumbnailUri = cursor.getString(cursor.getColumnIndex(PROJECTION[PHOTO_THUMBNAIL_URI_INDEX]));
				if (photoThumbnailUri != null) {
					contact.setPhoto(Uri.parse(photoThumbnailUri));
				}
				contact.setName(cursor.getString(cursor.getColumnIndex(PROJECTION[DISPLAY_NAME_INDEX])));
				contact.setStarred(cursor.getInt(cursor.getColumnIndex(PROJECTION[STARRED_INDEX])));
				contact.setTimesContacted(cursor.getInt(cursor.getColumnIndex(PROJECTION[TIMES_CONTACTED_INDEX])));
				contact.setLastTimeContacted(cursor.getLong(cursor.getColumnIndex(PROJECTION[LAST_TIME_CONTACTED_INDEX])));
				data.add(contact);
			} while (cursor.moveToNext());
		}
		this.cursor = cursor;
		notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		data = new ArrayList<Contact>();
		this.cursor = null;
	}
	
	public ArrayList<Contact> getData() {
		return data;
	}
	
	public Contact getSelectedContact() {
		return data.get(checkedItemPosition);
	}
	
	public String getSearchCriteria() {
		return searchCriteria;
	}
	
	public Cursor getCursor() {
		return cursor;
	}
	
	public void setCheckedItemPosition(int checkedItemPosition) {
		this.checkedItemPosition = checkedItemPosition;
	}	
	
	public int getCheckedItemPosition() {
		return checkedItemPosition;
	}
	
	public int getContactId() {
		
		if (cursor != null && checkedItemPosition >= 0) {
			cursor.moveToPosition(checkedItemPosition);
			return cursor.getInt(ID_INDEX);
		}
		return -1;
	}
	
	public String getContactLookupKey() {
		
		if (cursor != null && checkedItemPosition >= 0) {
			cursor.moveToPosition(checkedItemPosition);
			return cursor.getString(LOOKUP_KEY_INDEX);
		}
		return null;
	}
	
	public Uri getContactLookupUri() {
		
		if (cursor != null && checkedItemPosition >= 0) {
			cursor.moveToPosition(checkedItemPosition);
			int id = cursor.getInt(ID_INDEX);
			String lookupKey = cursor.getString(LOOKUP_KEY_INDEX);
			return Contacts.getLookupUri(id, lookupKey);
		}
		return Uri.EMPTY;
	}

}
