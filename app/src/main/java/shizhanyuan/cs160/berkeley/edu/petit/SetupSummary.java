package shizhanyuan.cs160.berkeley.edu.petit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.MenuOption;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.NotificationTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCardsException;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteToqNotification;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.CardImage;

import java.io.InputStream;

/**
 * Created by aracelisalcedo on 12/4/14.
 */
public class SetupSummary extends Activity {

    private Button getNewPet;
    private Button checkPoints;
    private Button adoption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_summary);

        getNewPet = (Button) findViewById(R.id.get_new_pet);
        checkPoints = (Button) findViewById(R.id.check_points);
        adoption = (Button) findViewById(R.id.adoption);
        checkPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent points = new Intent(SetupSummary.this, CheckPoints.class);
                startActivity(points);

            }
        });
        getNewPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPet = new Intent(SetupSummary.this, SelectPet.class);
                startActivity(newPet);
            }
        });

        adoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

    }

    private void sendNotification() {
        String[] message = new String[1];
        message[0] = "Some cleaning is needed";
        // Create a NotificationTextCard
        NotificationTextCard notificationCard = new NotificationTextCard(System.currentTimeMillis(),
                SelectOnePet.name +" Pooped!", message);

        // Draw divider between lines of text
        notificationCard.setShowDivider(false);
        // Vibrate to alert user when showing the notification
        notificationCard.setVibeAlert(true);
        // Create a notification with the NotificationTextCard we made
        RemoteToqNotification notification = new RemoteToqNotification(this, notificationCard);

        try {
            // Send the notification
            PetIt.mDeckOfCardsManager.sendNotification(notification);
            try {
                addNewCard(getBitmap("pooped.png"), 0);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Can't get picture icon");
                return;
            }
//            Toast.makeText(this, "Sent Notification", Toast.LENGTH_SHORT).show();

        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to send Notification", Toast.LENGTH_SHORT).show();
        }

    }

    protected void addNewCard(Bitmap bm, int type) {
        ListCard listCard = PetIt.mRemoteDeckOfCards.getListCard();
        SimpleTextCard simpleTextCard = (SimpleTextCard) listCard.get("card1");
        listCard.remove(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card1");
        simpleTextCard.setHeaderText(SelectOnePet.name);
        CardImage cardImage = new CardImage("card.image.1", bm);
        PetIt.mRemoteResourceStore.addResource(cardImage);
        simpleTextCard.setCardImage(PetIt.mRemoteResourceStore, cardImage);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        if (type == 0) {
            simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Clean        ", false)});
            simpleTextCard.setTitleText("Healthy & Annoyed");
        }
        if (type == 1) {
            simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Food", false), new MenuOption("-           Sleep", false), new MenuOption("-           Play", false), new MenuOption("-           Train", false)});
            simpleTextCard.setTitleText("Healthy & Happy");
        }
        listCard.add(0,simpleTextCard);

        try {
            PetIt.mDeckOfCardsManager.updateDeckOfCards(PetIt.mRemoteDeckOfCards, PetIt.mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap getBitmap(String fileName) throws Exception{

        try{
            InputStream is= getAssets().open(fileName);
            return BitmapFactory.decodeStream(is);
        }
        catch (Exception e){
            throw new Exception("An error occurred getting the bitmap: " + fileName, e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pet_it, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

