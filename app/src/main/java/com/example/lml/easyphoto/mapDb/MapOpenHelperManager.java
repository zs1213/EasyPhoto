package com.example.lml.easyphoto.mapDb;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class MapOpenHelperManager {

	private static int claimHelperCount = 0;
	private static volatile MapDatabaseMaisHelper claimHelper = null;
	

	public static synchronized MapDatabaseMaisHelper getClaimHelper(Context ctx) {
		if (claimHelper == null) {
			try {
				claimHelper = new MapDatabaseMaisHelper(ctx, null);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			claimHelperCount = 0;
		}
		claimHelperCount++;
		return claimHelper;
	}

	public static synchronized void closeClaimHelper() {
		claimHelperCount--;
		if (claimHelperCount == 0) {
			if (claimHelper != null) {
				claimHelper.close();
			}
		} else if (claimHelperCount < 0) {
			throw new IllegalStateException("Too many calls to release helper. ClaimHelper Instance count = " + claimHelperCount);
		}
	}
}
