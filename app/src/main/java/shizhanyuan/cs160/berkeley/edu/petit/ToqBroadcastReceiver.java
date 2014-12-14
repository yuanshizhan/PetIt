package shizhanyuan.cs160.berkeley.edu.petit;

/**
 * Created by stevenchen on 12/7/14.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ToqBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the demo app activity to complete the install of the deck of cards applet
        Intent launchIntent= new Intent(context, SelectPet.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(launchIntent);
    }
}