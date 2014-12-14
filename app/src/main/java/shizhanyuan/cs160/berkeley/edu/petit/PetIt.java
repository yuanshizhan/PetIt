package shizhanyuan.cs160.berkeley.edu.petit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class PetIt extends Activity {

    private final static String PREFS_FILE= "prefs_file";
    private final static String DECK_OF_CARDS_KEY= "deck_of_cards_key";
    private final static String DECK_OF_CARDS_VERSION_KEY= "deck_of_cards_version_key";

    protected static DeckOfCardsManager mDeckOfCardsManager;
    protected static RemoteDeckOfCards mRemoteDeckOfCards;
    protected static RemoteResourceStore mRemoteResourceStore;
    private CardImage[] mCardImages;
    private ToqBroadcastReceiver toqReceiver;
    protected static DeckOfCardsEventListener deckOfCardsEventListener;
    private Bitmap fetchedBM;
    private boolean inZone = false;
    private Button signUpButton;
    private Button doneButton;
    private TextView enterNameText;
    private EditText enterName;
    private Button continueButton;
    public static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        mDeckOfCardsManager = DeckOfCardsManager.getInstance(getApplicationContext());
        toqReceiver = new ToqBroadcastReceiver();
        deckOfCardsEventListener= new DeckOfCardsEventListenerImpl();

        // Acquire a reference to the system Location Manager
        setContentView(R.layout.home_page);

        signUpButton = (Button) findViewById(R.id.sign_up_button);
        enterNameText = (TextView) findViewById(R.id.please_text);
        doneButton = (Button) findViewById(R.id.done_sign_up_button);
        enterName = (EditText) findViewById(R.id.name_input);
        continueButton = (Button) findViewById(R.id.continue_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uninstall();
                //switch to enter name page
                signUpButton.setVisibility(view.GONE);
                continueButton.setVisibility(view.GONE);
                enterNameText.setVisibility(view.VISIBLE);
                doneButton.setVisibility(view.VISIBLE);
                enterName.setVisibility(view.VISIBLE);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                install();
                userName = enterName.getText().toString();
                Intent i = new Intent(PetIt.this, SelectOnePet.class);
                startActivity(i);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cont = new Intent(PetIt.this, SetupSummary.class);
                startActivity(cont);
            }
        });

        init();
    }


    protected void addNewCard(Bitmap bm, int type) {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        SimpleTextCard simpleTextCard = (SimpleTextCard) listCard.get("card1");
        String oldCardName = simpleTextCard.getHeaderText();
        listCard.remove(simpleTextCard);
        simpleTextCard = new SimpleTextCard("card1");
        simpleTextCard.setHeaderText(oldCardName);
        CardImage cardImage = new CardImage("card.image.1", bm);
        mRemoteResourceStore.addResource(cardImage);
        simpleTextCard.setCardImage(mRemoteResourceStore, cardImage);
        simpleTextCard.setTitleText("Healthy & Happy");
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        if (type == 0)
            simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Clean        ", false)});
        if (type == 1)
            simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Food", false), new MenuOption("-           Sleep", false), new MenuOption("-           Play", false), new MenuOption("-           Train", false)});
        listCard.add(0,simpleTextCard);

        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to add new pet.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @see android.app.Activity#onStart()
     * This is called after onCreate(Bundle) or after onRestart() if the activity has been stopped
     */
    protected void onStart(){
        super.onStart();
        mDeckOfCardsManager.addDeckOfCardsEventListener(deckOfCardsEventListener);
        Log.d(Constants.TAG, "ToqApiDemo.onStart");
        // If not connected, try to connect
        if (!mDeckOfCardsManager.isConnected()){
            try{
                mDeckOfCardsManager.connect();
            }
            catch (RemoteDeckOfCardsException e){
                e.printStackTrace();
            }
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
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Installs applet to Toq watch if app is not yet installed
     */
    private void install() {
        boolean isInstalled = true;

        try {
            isInstalled = mDeckOfCardsManager.isInstalled();
        }
        catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Can't determine if app is installed", Toast.LENGTH_SHORT).show();
        }

        if (!isInstalled) {
            try {
                mDeckOfCardsManager.installDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
                Toast.makeText(this, "Installed application.", Toast.LENGTH_SHORT).show();
            } catch (RemoteDeckOfCardsException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: Cannot install application", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "App is already installed!", Toast.LENGTH_SHORT).show();
        }

        try{
            storeDeckOfCards();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uninstall() {
        boolean isInstalled = true;

        try {
            isInstalled = mDeckOfCardsManager.isInstalled();
        }
        catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Can't determine if app is installed", Toast.LENGTH_SHORT).show();
        }

        if (isInstalled) {
            try{
                mDeckOfCardsManager.uninstallDeckOfCards();
            }
            catch (RemoteDeckOfCardsException e){
                Toast.makeText(this, "Failed to uninstall", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(this, "Uninstalled", Toast.LENGTH_SHORT).show();
        }
    }



    // Initialise
    private void init(){

        // Create the resource store for icons and images
        mRemoteResourceStore= new RemoteResourceStore();

        DeckOfCardsLauncherIcon whiteIcon = null;
        DeckOfCardsLauncherIcon colorIcon = null;

        // Get the launcher icons
        try{
            whiteIcon= new DeckOfCardsLauncherIcon("white.launcher.icon", getBitmap("bw.png"), DeckOfCardsLauncherIcon.WHITE);
            colorIcon= new DeckOfCardsLauncherIcon("color.launcher.icon", getBitmap("color.png"), DeckOfCardsLauncherIcon.COLOR);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Can't get launcher icon");
            return;
        }

        mCardImages = new CardImage[6];
        try{
            mCardImages[0]= new CardImage("card.image.1", getBitmap("happy.png"));
            mCardImages[1]= new CardImage("card.image.2", getBitmap("happy.png"));
            mCardImages[2]= new CardImage("card.image.3", getBitmap("happy.png"));
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }

        // Try to retrieve a stored deck of cards
        try {
            // If there is no stored deck of cards or it is unusable, then create new and store
            if ((mRemoteDeckOfCards = getStoredDeckOfCards()) == null){
                mRemoteDeckOfCards = createDeckOfCards();
                storeDeckOfCards();
            }
        }
        catch (Throwable th){
            th.printStackTrace();
            mRemoteDeckOfCards = null; // Reset to force recreate
        }
        Logger.d("CARDS ARE NULL: " + (mRemoteDeckOfCards.toString()));
        // Make sure in usable state
//        if (mRemoteDeckOfCards == null){
        mRemoteDeckOfCards = createDeckOfCards();
//        }

        // Set the custom launcher icons, adding them to the resource store
        mRemoteDeckOfCards.setLauncherIcons(mRemoteResourceStore, new DeckOfCardsLauncherIcon[]{whiteIcon, colorIcon});

        // Re-populate the resource store with any card images being used by any of the cards
        for (Iterator<Card> it= mRemoteDeckOfCards.getListCard().iterator(); it.hasNext();){

            String cardImageId= ((SimpleTextCard)it.next()).getCardImageId();

            if ((cardImageId != null) && !mRemoteResourceStore.containsId(cardImageId)){

                if (cardImageId.equals("card.image.1")){
                    mRemoteResourceStore.addResource(mCardImages[0]);
                }

            }
        }
    }

    // Read an image from assets and return as a bitmap
    private Bitmap getBitmap(String fileName) throws Exception{

        try{
            InputStream is= getAssets().open(fileName);
            return BitmapFactory.decodeStream(is);
        }
        catch (Exception e){
            throw new Exception("An error occurred getting the bitmap: " + fileName, e);
        }
    }

    private RemoteDeckOfCards getStoredDeckOfCards() throws Exception{

        if (!isValidDeckOfCards()){
            Log.w(Constants.TAG, "Stored deck of cards not valid for this version of the demo, recreating...");
            return null;
        }

        SharedPreferences prefs= getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String deckOfCardsStr= prefs.getString(DECK_OF_CARDS_KEY, null);

        if (deckOfCardsStr == null){
            return null;
        }
        else{
            return ParcelableUtil.unmarshall(deckOfCardsStr, RemoteDeckOfCards.CREATOR);
        }

    }

    /**
     * Uses SharedPreferences to store the deck of cards
     * This is mainly used to
     */
    private void storeDeckOfCards() throws Exception{
        // Retrieve and hold the contents of PREFS_FILE, or create one when you retrieve an editor (SharedPreferences.edit())
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        // Create new editor with preferences above
        SharedPreferences.Editor editor = prefs.edit();
        // Store an encoded string of the deck of cards with key DECK_OF_CARDS_KEY
        editor.putString(DECK_OF_CARDS_KEY, ParcelableUtil.marshall(mRemoteDeckOfCards));
        // Store the version code with key DECK_OF_CARDS_VERSION_KEY
        editor.putInt(DECK_OF_CARDS_VERSION_KEY, Constants.VERSION_CODE);
        // Commit these changes
        editor.commit();
    }

    // Check if the stored deck of cards is valid for this version of the demo
    private boolean isValidDeckOfCards(){

        SharedPreferences prefs= getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        // Return 0 if DECK_OF_CARDS_VERSION_KEY isn't found
        int deckOfCardsVersion= prefs.getInt(DECK_OF_CARDS_VERSION_KEY, 0);

        return deckOfCardsVersion >= Constants.VERSION_CODE;
    }

    // Create some cards with example content
    private RemoteDeckOfCards createDeckOfCards(){

        ListCard listCard= new ListCard();

//        SimpleTextCard simpleTextCard= new SimpleTextCard("card1");
//        simpleTextCard.setHeaderText("Max");
//        simpleTextCard.setTitleText("Healthy & Happy");
//
//        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[0]);
//        simpleTextCard.setReceivingEvents(true);
//        simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Food", false), new MenuOption("-           Sleep", false), new MenuOption("-           Play", false), new MenuOption("-           Train", false)});
//
//        listCard.add(simpleTextCard);

//        // Card #2
//        simpleTextCard= new SimpleTextCard("card2");
//        simpleTextCard.setHeaderText("Joann");
//        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[1]);
//        simpleTextCard.setReceivingEvents(true);
//        simpleTextCard.setTitleText("Healthy & Happy");
//
//        simpleTextCard.setShowDivider(true);
//        simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Food", false), new MenuOption("-           Sleep", false), new MenuOption("-           Play", false), new MenuOption("-           Train", false)});
//        listCard.add(simpleTextCard);
//
//        // Card #3
//        simpleTextCard= new SimpleTextCard("card3");
//        simpleTextCard.setHeaderText("Michael");
//        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[2]);
//        simpleTextCard.setReceivingEvents(true);
//        simpleTextCard.setTitleText("Healthy & Happy");
//
//        simpleTextCard.setShowDivider(true);
//        simpleTextCard.setMenuOptionObjs(new MenuOption[]{new MenuOption("-           Food", false), new MenuOption("-           Sleep", false), new MenuOption("-           Play", false), new MenuOption("-           Train", false)});
//        listCard.add(simpleTextCard);

        return new RemoteDeckOfCards(this, listCard);
    }

    private class DeckOfCardsEventListenerImpl implements DeckOfCardsEventListener {

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardOpen(java.lang.String)
         */
        public void onCardOpen(final String cardId){
            runOnUiThread(new Runnable(){
                public void run(){
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardVisible(java.lang.String)
         */
        public void onCardVisible(final String cardId){
//            runOnUiThread(new Runnable(){
//                public void run(){
//                    Toast.makeText(ToqActivity.this, getString(R.string.opened) + cardId, Toast.LENGTH_SHORT).show();
//
//                }
//
//            });
            return;
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardInvisible(java.lang.String)
         */
        public void onCardInvisible(final String cardId){
//            runOnUiThread(new Runnable(){
//                public void run(){
//                    Toast.makeText(ToqActivity.this, getString(R.string.opened) + cardId, Toast.LENGTH_SHORT).show();
//                }
//            });
            return;
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onCardClosed(java.lang.String)
         */
        public void onCardClosed(final String cardId){
            runOnUiThread(new Runnable(){
                public void run(){
                }
            });
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onMenuOptionSelected(java.lang.String, java.lang.String)
         */
        public void onMenuOptionSelected(final String cardId, final String menuOption){
            runOnUiThread(new Runnable(){
                public void run(){
                }
            });
            try {
                addNewCard(getBitmap("happy.png"),1);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Can't get picture icon");
                return;
            }
        }

        /**
         * @see com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener#onMenuOptionSelected(java.lang.String, java.lang.String, java.lang.String)
         */
        public void onMenuOptionSelected(final String cardId, final String menuOption, final String quickReplyOption){
//            runOnUiThread(new Runnable(){
//                public void run(){
//                    Toast.makeText(ToqActivity.this, getString(R.string.opened) + cardId + " [" + menuOption + ":" + quickReplyOption +
//                            "]", Toast.LENGTH_SHORT).show();
//                }
//            });
        }

    }


}