package com.example.helpers.metadata;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

	private UserDetails user;
	
	public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
		public UserInfo createFromParcel(Parcel in) {
			return new UserInfo(in);
		}

		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
	};

	public UserInfo() {}
	
	private UserInfo(Parcel in) {
		user = (UserDetails) in.readSerializable();
	}

	public UserInfo(UserDetails user) {
		this.user = user;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(user);
	}

	
	public UserDetails getUserDetail() {
		return user;
	}

	public void setUserDetail(UserDetails userDetail) {
		this.user = userDetail;
	}

}
