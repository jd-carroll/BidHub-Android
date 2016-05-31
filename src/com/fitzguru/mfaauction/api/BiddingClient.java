package com.fitzguru.mfaauction.api;

import android.app.Activity;
import android.util.Log;

import com.fitzguru.mfaauction.IdentityManager;
import com.fitzguru.mfaauction.models.AuctionItem;
import com.fitzguru.mfaauction.models.Bid;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class BiddingClient {

  static DataManager data = DataManager.getInstance();

  public static void placeBid(final AuctionItem item, final int amt, final DataManager.BidCallback after, Activity context) {

    // Add our bid and confirm that it's the latest and highest bid afterward
    final Bid bid = new Bid();
    bid.setRelatedItemId(item.getObjectId());
    bid.setAmount(amt);
    bid.setName(IdentityManager.getName(context).length() < 2 ? IdentityManager.getEmail(context) : IdentityManager.getName(context));
    bid.setTelephone(IdentityManager.getTelephone(context));
    bid.setEmail(IdentityManager.getEmail(context));
    bid.saveInBackground(new SaveCallback() {
      @Override
      public void done(final ParseException e) {
        ParsePush.subscribeInBackground("a" + item.getObjectId(), new SaveCallback() {
          @Override
          public void done(ParseException e) {
            Log.i("TEST", "Subscribed.");
          }
        });

        data.fetchAllItems(new Runnable() {
          @Override
          public void run() {
            if (after != null)
              after.bidResult(e == null);
          }
        });
      }
    });
  }
}
