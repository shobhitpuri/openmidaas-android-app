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
package org.openmidaas.app.session.attributeset;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.openmidaas.app.activities.AuthorizationActivity;
import org.openmidaas.app.activities.PhoneRegistrationActivity;
import org.openmidaas.app.common.Constants;
import org.openmidaas.app.common.Logger;
import org.openmidaas.app.session.AttributeFetchException;
import org.openmidaas.library.model.PhoneAttribute;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;
import org.openmidaas.library.persistence.core.PhoneNumberDataCallback;

import android.app.Activity;
import android.content.Intent;

public class PhoneAttributeSet extends AbstractAttributeSet {

	private boolean mRetrievalSuccess = false;
	
	public PhoneAttributeSet() {
		mType = Constants.AttributeNames.PHONE;
	}
	
	@Override
	public void fetch() throws AttributeFetchException {
		mRetrievalSuccess = false;
		final CountDownLatch MUTEX = new CountDownLatch(1);
		AttributePersistenceCoordinator.getPhoneNumbers(new PhoneNumberDataCallback() {
			
			@Override
			public void onError(MIDaaSException exception) {
				Logger.debug(getClass(), exception.getError().getErrorMessage());
				mRetrievalSuccess = false;
				MUTEX.countDown();
			}
			
			@Override
			public void onSuccess(List<PhoneAttribute> phoneList) {
				Logger.debug(getClass(), phoneList.toString());
				mRetrievalSuccess = true;
				mAttributeList.addAll(phoneList);
				MUTEX.countDown();
			}
		});
		
		try {
			MUTEX.await(TIMEOUT, TIME_UNIT);
			if(!mRetrievalSuccess) {
				throw new AttributeFetchException("Attribute could not be retrieved from persistence store.");
			}
		} catch (InterruptedException e) {
			Logger.error(getClass(), e.getMessage());
			throw new AttributeFetchException("Operation to retrieve value from persistence store timed out.");
		}

	}

	@Override
	public void onModify(Activity activity) {
		activity.startActivityForResult(new Intent(activity, PhoneRegistrationActivity.class),  AuthorizationActivity.AUTHORIZATION_ACTIVITY_REQUEST_CODE);

	}

}
