/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.app.activities.ui.list;

import org.openmidaas.app.R;
import org.openmidaas.app.common.CategoryMap;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Utils;
import org.openmidaas.library.model.GenericAttribute;
import org.openmidaas.library.model.core.AbstractAttribute;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * 
 * Personal category
 *
 */
public class PersonalListCategory extends AbstractListCategory {
	
	private String[] genders = null;
	
	private Activity mActivity;
	
	private AlertDialog mGenderDialog = null;
	
	public PersonalListCategory() {
		mGroupName = Constants.ATTRIBUTE_CATEGORY_PERSONAL;
	}

	@Override
	public void onAddButtonTouch(Activity activity) {
		showPersonalInfoChoicesDialog(activity);
	}

	@Override
	public void addAttribute(AbstractAttribute<?> attribute) {
		CategoryMap map = CategoryMap.get(attribute.getName());
		if(map != null) {
			addToPosition(attribute, map.ordinal());
		} else {
			mList.add(new GenericAttributeListElement((GenericAttribute) attribute));
		}
	}
	
	private void addToPosition(AbstractAttribute<?> attribute, int position) {
		if(attribute.getName().equals(Constants.AttributeNames.BIRTHDAY)) {
			mList.add(new BirthdayListElement((GenericAttribute) attribute));
		} else if (attribute.getName().equals(Constants.AttributeNames.GENDER)) {
			mList.add(new GenderListElement((GenericAttribute) attribute));
		} else {
			GenericAttribute genericAttribute = (GenericAttribute) mList.get(position).getAttribute();
			if(genericAttribute.getValue() != null && !(genericAttribute.getValue().isEmpty())) {
				mList.add(new GenericAttributeListElement((GenericAttribute) attribute));
			} else {
				mList.get(position).setAttribute(attribute);
			}
		}
	}
	
	private void showPersonalInfoChoicesDialog(final Activity activity) {
	  mActivity = activity;
	  final String[] personalInfoNames = activity.getResources().getStringArray(R.array.additionalPersonalInfoArray);
	  genders = activity.getResources().getStringArray(R.array.genders);
	  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setTitle(activity.getString(R.string.addMorePersonalInfo));
      builder.setSingleChoiceItems(personalInfoNames, -1, new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(personalInfoNames[which].equals("Birthday")) {
				DialogUtils.showBirthdayDatePickerDialog(activity, null);
			} else  if (personalInfoNames[which].equals("Gender")) {
				DialogUtils.showRadioButtonDialog(activity, activity.getString(R.string.genderDialogTitle), genders, genderDialogListener);
			}
		}
      });
      mGenderDialog = builder.create();
      mGenderDialog.show();
	}
	
	private DialogInterface.OnClickListener genderDialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			String gender = genders[which];
			Utils.createGenericAttribute(mActivity, Constants.AttributeNames.GENDER, gender, null);
			dialogInterface.dismiss();
		}
	};
}
