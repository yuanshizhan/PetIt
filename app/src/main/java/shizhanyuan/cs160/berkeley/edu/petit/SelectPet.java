package shizhanyuan.cs160.berkeley.edu.petit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.Constants;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.Card;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.MenuOption;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.NotificationTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.DeckOfCardsManager;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCards;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCardsException;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteResourceStore;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteToqNotification;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.CardImage;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.DeckOfCardsLauncherIcon;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.util.Logger;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.util.ParcelableUtil;

import java.io.InputStream;

/**
 * Created by aracelisalcedo on 12/4/14.
 */
public class SelectPet extends Activity{

    private ImageButton catButton;
    private ImageButton dogButton;
    private ImageButton fluffy;
    private ImageButton blackFluffy;

    private View selectRectangle1;
    private View selectRectangle2;
    private View selectRectangle3;
    private View selectRectangle4;
    private Button cancel_name_pet;

    private Button doneNamePet;

    private final static String PREFS_FILE= "prefs_file";
    private final static String DECK_OF_CARDS_KEY= "deck_of_cards_key";
    private final static String DECK_OF_CARDS_VERSION_KEY= "deck_of_cards_version_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pet);


        doneNamePet = (Button) findViewById(R.id.done_name_pet);

        selectRectangle1 = (View) findViewById(R.id.select_rectangle1);
        selectRectangle2 = (View) findViewById(R.id.select_rectangle2);
        selectRectangle3 = (View) findViewById(R.id.select_rectangle3);
        selectRectangle4 = (View) findViewById(R.id.select_rectangle4);

        catButton = (ImageButton) findViewById(R.id.cat_button);
        dogButton = (ImageButton) findViewById(R.id.dog_button);
        fluffy = (ImageButton) findViewById(R.id.fluffy_button);
        blackFluffy = (ImageButton) findViewById(R.id.black_fluffy_button);

        doneNamePet = (Button) findViewById(R.id.done_name_pet);
        cancel_name_pet = (Button) findViewById(R.id.cancel_name_pet);
        catButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectRectangle1.setVisibility(view.VISIBLE);
                selectRectangle2.setVisibility(view.INVISIBLE);
                selectRectangle3.setVisibility(view.INVISIBLE);
                selectRectangle4.setVisibility(view.INVISIBLE);

            }
        });

        dogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectRectangle1.setVisibility(view.INVISIBLE);
                selectRectangle2.setVisibility(view.VISIBLE);
                selectRectangle3.setVisibility(view.INVISIBLE);
                selectRectangle4.setVisibility(view.INVISIBLE);
            }
        });

        fluffy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectRectangle1.setVisibility(view.INVISIBLE);
                selectRectangle3.setVisibility(view.VISIBLE);
                selectRectangle2.setVisibility(view.INVISIBLE);
                selectRectangle4.setVisibility(view.INVISIBLE);

            }
        });

        blackFluffy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectRectangle1.setVisibility(view.INVISIBLE);
                selectRectangle2.setVisibility(view.INVISIBLE);
                selectRectangle3.setVisibility(view.INVISIBLE);
                selectRectangle4.setVisibility(view.VISIBLE);

            }
        });

        doneNamePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText petName = (EditText) findViewById(R.id.name_input);
                String name = petName.getText().toString();
                try {
                    addNewPet(getBitmap("happy.png"), name);
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Can't get picture icon");
                    return;
                }
                Intent i = new Intent(SelectPet.this, SetupSummary.class);
                startActivity(i);
            }
        });

        cancel_name_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectPet.this, PetIt.class);
                startActivity(i);
            }
        });

    }

    protected void addNewPet(Bitmap bm, String name) {
        ListCard listCard = PetIt.mRemoteDeckOfCards.getListCard();
        System.err.print(listCard.size()+1);
        SimpleTextCard simpleTextCard = new SimpleTextCard("card"+(listCard.size()+1));
        simpleTextCard.setHeaderText(name);
        CardImage cardImage = new CardImage("card.image.4", bm);
        PetIt.mRemoteResourceStore.addResource(cardImage);
        simpleTextCard.setCardImage(PetIt.mRemoteResourceStore, cardImage);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        simpleTextCard.setTitleText("Healthy & Happy");
        simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Food", false), new MenuOption("-           Sleep", false), new MenuOption("-           Play", false), new MenuOption("-           Train", false)});
        listCard.add(simpleTextCard);

        try {
            PetIt.mDeckOfCardsManager.updateDeckOfCards(PetIt.mRemoteDeckOfCards, PetIt.mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to add a new pet.", Toast.LENGTH_SHORT).show();
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

