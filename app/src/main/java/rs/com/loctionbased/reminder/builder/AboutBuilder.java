package rs.com.loctionbased.reminder.builder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.view.View;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.util.ColorUtil;
import rs.com.loctionbased.reminder.util.IconUtil;
import rs.com.loctionbased.reminder.util.IntentUtil;
import rs.com.loctionbased.reminder.views.AboutView;

import java.util.LinkedList;

import static rs.com.loctionbased.reminder.R.mipmap.share;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class AboutBuilder {

    private Context context;
    private IntentUtil util;

    private String name;
    private String subTitle;
    private String brief;
    private String appName;
    private String appTitle;

    private Bitmap photo;
    private Bitmap cover;
    private Bitmap appIcon;

    private int nameColor;
    private int subTitleColor;
    private int briefColor;
    private int iconColor;
    private int backgroundColor;

    private boolean showDivider = true;
    private int dividerColor = 0;
    private int dividerHeight = 4;
    private int dividerDashWidth = 15;
    private int dividerDashGap = 15;

    private boolean linksAnimated = true;
    private int linksColumnsCount = 5;
    private int actionsColumnsCount = 2;

    private boolean wrapScrollView = false;
    private boolean showAsCard = true;

    private LinkedList<Item> links = new LinkedList<>();
    private LinkedList<Item> actions = new LinkedList<>();

    @Deprecated
    AboutBuilder(Context context) {
        this.context = context;
        this.util = new IntentUtil(context);
    }

    public static AboutBuilder with(Context context) {
        return new AboutBuilder(context);
    }

    private String getApplicationID() {
        return context.getPackageName();
    }

    private PackageInfo getPackageInfo() throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(getApplicationID(), 0);
    }

    public int getLastActionId() {
        return getLastAction().getId();
    }

    public Item getLastAction() {
        return actions.getLast();
    }

    public int getLastLinkId() {
        return getLastLink().getId();
    }

    public Item getLastLink() {
        return links.getLast();
    }

    @NonNull
    public AboutBuilder setName(String text) {
        this.name = text;
        return this;
    }

    @NonNull
    public AboutBuilder setName(int text) {
        return setName(context.getString(text));
    }

    @NonNull
    public AboutBuilder setSubTitle(String text) {
        this.subTitle = text;
        return this;
    }

    @NonNull
    public AboutBuilder setSubTitle(int text) {
        return setSubTitle(context.getString(text));
    }

    @NonNull
    public AboutBuilder setBrief(String text) {
        this.brief = text;
        return this;
    }

    @NonNull
    public AboutBuilder setBrief(int text) {
        return setBrief(context.getString(text));
    }

    @NonNull
    public AboutBuilder setAppName(String text) {
        this.appName = text;
        return this;
    }

    @NonNull
    public AboutBuilder setAppName(int text) {
        return setAppName(context.getString(text));
    }

    @NonNull
    public AboutBuilder setAppTitle(String text) {
        this.appTitle = text;
        return this;
    }

    @NonNull
    public AboutBuilder setAppTitle(int text) {
        return setAppTitle(context.getString(text));
    }

    @NonNull
    public AboutBuilder setVersionNameAsAppSubTitle() {
        try {
            return setAppTitle(context.getString(R.string.version, getPackageInfo().versionName));
        } catch (PackageManager.NameNotFoundException e) {
            return setAppTitle(R.string.error);
        }
    }

    @NonNull
    public AboutBuilder setPhoto(Bitmap photo) {
        this.photo = photo;
        return this;
    }

    @NonNull
    public AboutBuilder setPhoto(int photo) {
        return setPhoto(IconUtil.getBitmap(context, photo));
    }

    @NonNull
    public AboutBuilder setPhoto(@NonNull BitmapDrawable photo) {
        return setPhoto(IconUtil.getBitmap(photo));
    }

    @NonNull
    public AboutBuilder setCover(Bitmap cover) {
        this.cover = cover;
        return this;
    }

    @NonNull
    public AboutBuilder setCover(int cover) {
        return setCover(IconUtil.getBitmap(context, cover));
    }

    @NonNull
    public AboutBuilder setCover(@NonNull BitmapDrawable cover) {
        return setCover(IconUtil.getBitmap(cover));
    }

    @NonNull
    public AboutBuilder setAppIcon(Bitmap icon) {
        this.appIcon = icon;
        return this;
    }

    @NonNull
    public AboutBuilder setAppIcon(int icon) {
        return setAppIcon(IconUtil.getBitmap(context, icon));
    }

    @NonNull
    public AboutBuilder setAppIcon(@NonNull BitmapDrawable icon) {
        return setAppIcon(IconUtil.getBitmap(icon));
    }

    @NonNull
    public AboutBuilder setNameColor(int color) {
        this.nameColor = ColorUtil.get(context, color);
        return this;
    }

    @NonNull
    public AboutBuilder setSubTitleColor(int color) {
        this.subTitleColor = ColorUtil.get(context, color);
        return this;
    }


    @NonNull
    public AboutBuilder setBriefColor(int color) {
        this.briefColor = ColorUtil.get(context, color);
        return this;
    }


    @NonNull
    public AboutBuilder setDividerColor(int color) {
        this.dividerColor = ColorUtil.get(context, color);
        return this;
    }

    @NonNull
    public AboutBuilder setIconColor(int color) {
        this.iconColor = ColorUtil.get(context, color);
        return this;
    }

    @NonNull
    public AboutBuilder setBackgroundColor(int color) {
        this.backgroundColor = ColorUtil.get(context, color);
        return this;
    }

    @NonNull
    public AboutBuilder setActionsColumnsCount(int count) {
        this.actionsColumnsCount = count;
        return this;
    }

    @NonNull
    public AboutBuilder setLinksColumnsCount(int count) {
        this.linksColumnsCount = count;
        return this;
    }

    @NonNull
    public AboutBuilder setLinksAnimated(boolean animate) {
        this.linksAnimated = animate;
        return this;
    }

    @NonNull
    public AboutBuilder setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
        return this;
    }

    @NonNull
    public AboutBuilder setDividerDashWidth(int dividerDashWidth) {
        this.dividerDashWidth = dividerDashWidth;
        return this;
    }

    @NonNull
    public AboutBuilder setDividerDashGap(int dividerDashGap) {
        this.dividerDashGap = dividerDashGap;
        return this;
    }

    @NonNull
    public AboutBuilder setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
        return this;
    }

    @NonNull
    public AboutBuilder setWrapScrollView(boolean wrapScrollView) {
        this.wrapScrollView = wrapScrollView;
        return this;
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, String label, View.OnClickListener onClickListener) {
        links.add(new Item(icon, label, onClickListener));
        return this;
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, String label, Intent intent) {
        return addLink(icon, label, util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, String label, Uri uri) {
        return addLink(icon, label, util.clickUri(uri));
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, String label, String url) {
        return addLink(icon, label, Uri.parse(url));
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, int label, View.OnClickListener onClickListener) {
        return addLink(icon, context.getString(label), onClickListener);
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, int label, Intent intent) {
        return addLink(icon, label, util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, int label, Uri uri) {
        return addLink(icon, label, util.clickUri(uri));
    }

    @NonNull
    public AboutBuilder addLink(Bitmap icon, int label, String url) {
        return addLink(icon, label, Uri.parse(url));
    }

    @NonNull
    public AboutBuilder addLink(int icon, int label, View.OnClickListener onClickListener) {
        return addLink(IconUtil.getBitmap(context, icon), context.getString(label), onClickListener);
    }

    @NonNull
    public AboutBuilder addLink(int icon, int label, Intent intent) {
        return addLink(icon, label, util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addLink(int icon, int label, Uri uri) {
        return addLink(icon, label, util.clickUri(uri));
    }

    @NonNull
    public AboutBuilder addLink(int icon, int label, String url) {
        return addLink(icon, label, Uri.parse(url));
    }

    @NonNull
    public AboutBuilder addLink(int icon, String label, View.OnClickListener onClickListener) {
        return addLink(IconUtil.getBitmap(context, icon), label, onClickListener);
    }

    @NonNull
    public AboutBuilder addLink(int icon, String label, Intent intent) {
        return addLink(icon, label, util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addLink(int icon, String label, Uri uri) {
        return addLink(icon, label, util.clickUri(uri));
    }

    @NonNull
    public AboutBuilder addLink(int icon, String label, String url) {
        return addLink(icon, label, Uri.parse(url));
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, int label, View.OnClickListener onClickListener) {
        return addLink(IconUtil.getBitmap(icon), context.getString(label), onClickListener);
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, int label, Intent intent) {
        return addLink(icon, label, util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, int label, Uri uri) {
        return addLink(icon, label, util.clickUri(uri));
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, int label, String url) {
        return addLink(icon, label, Uri.parse(url));
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, String label, View.OnClickListener onClickListener) {
        return addLink(IconUtil.getBitmap(icon), label, onClickListener);
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, String label, Intent intent) {
        return addLink(icon, label, util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, String label, Uri uri) {
        return addLink(icon, label, util.clickUri(uri));
    }

    @NonNull
    public AboutBuilder addLink(@NonNull BitmapDrawable icon, String label, String url) {
        return addLink(icon, label, Uri.parse(url));
    }

    @NonNull
    public AboutBuilder addGitHubLink(int user) {
        return addGitHubLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addGitHubLink(String user) {
        return addLink(R.mipmap.github, R.string.github, util.uri(R.string.url_github, user));
    }

    @NonNull
    public AboutBuilder addBitbucketLink(int user) {
        return addBitbucketLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addBitbucketLink(String user) {
        return addLink(R.mipmap.bitbucket, R.string.github, util.uri(R.string.url_bitbucket, user));
    }

    @NonNull
    public AboutBuilder addFacebookLink(int user) {
        return addFacebookLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addFacebookLink(String user) {
        return addLink(R.mipmap.facebook, R.string.facebook, util.openFacebook(user));
    }

    @NonNull
    public AboutBuilder addInstagramLink(int user) {
        return addInstagramLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addInstagramLink(String user) {
        return addLink(R.mipmap.instagram, R.string.instagram, util.openInstagram(user));
    }

    @NonNull
    public AboutBuilder addTwitterLink(int user) {
        return addTwitterLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addTwitterLink(String user) {
        return addLink(R.mipmap.twitter, R.string.twitter, util.openTwitter(user));
    }

    @NonNull
    public AboutBuilder addGoogleLink(int url) {
        return addGoogleLink(context.getString(url));
    }

    @NonNull
    public AboutBuilder addGoogleLink(String url) {
        return addLink(R.mipmap.google, R.string.google, url);
    }

    @NonNull
    public AboutBuilder addGooglePlusLink(int user) {
        return addGooglePlusLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addGooglePlusLink(String user) {
        return addLink(R.mipmap.google_plus, R.string.google_plus, util.openGooglePlus(user));
    }

    @NonNull
    public AboutBuilder addGooglePlayStoreLink(int user) {
        return addGooglePlayStoreLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addGooglePlayStoreLink(String user) {
        return addLink(R.mipmap.google_play_store, R.string.google_play_store, util.openGooglePlayDev(user));
    }

    @NonNull
    public AboutBuilder addGoogleGamesLink(int url) {
        return addGoogleGamesLink(context.getString(url));
    }

    @NonNull
    public AboutBuilder addGoogleGamesLink(String url) {
        return addLink(R.mipmap.google_play_games, R.string.google_play_games, url);
    }

    @NonNull
    public AboutBuilder addYoutubeChannelLink(int user) {
        return addYoutubeChannelLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addYoutubeChannelLink(String user) {
        return addLink(R.mipmap.youtube, R.string.youtube, util.openYoutubeChannel(user));
    }

    @NonNull
    public AboutBuilder addYoutubeUserLink(int user) {
        return addYoutubeUserLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addYoutubeUserLink(String user) {
        return addLink(R.mipmap.youtube, R.string.youtube, util.openYoutubeUser(user));
    }

    @NonNull
    public AboutBuilder addLinkedInLink(int user) {
        return addLinkedInLink(context.getString(user));
    }

    @NonNull
    public AboutBuilder addLinkedInLink(String user) {
        return addLink(R.mipmap.linkedin, R.string.linkedin, util.openLinkedIn(user));
    }

    @NonNull
    public AboutBuilder addSkypeLink(int phone) {
        return addSkypeLink(context.getString(phone));
    }

    @NonNull
    public AboutBuilder addSkypeLink(String phone) {
        return addLink(R.mipmap.skype, R.string.skype, util.openSkype(phone));
    }

    @NonNull
    public AboutBuilder addWhatsappLink(int name, int phone) {
        return addWhatsappLink(context.getString(name), context.getString(phone));
    }

    @NonNull
    public AboutBuilder addWhatsappLink(String name, String phone) {
        return addLink(R.mipmap.whatsapp, R.string.whastapp, util.openAddContact(name, phone));
    }

    @NonNull
    public AboutBuilder addAndroidLink(int url) {
        return addAndroidLink(context.getString(url));
    }

    @NonNull
    public AboutBuilder addAndroidLink(String url) {
        return addLink(R.mipmap.android, R.string.android, url);
    }

    @NonNull
    public AboutBuilder addDribbbleLink(int url) {
        return addDribbbleLink(context.getString(url));
    }

    @NonNull
    public AboutBuilder addDribbbleLink(String url) {
        return addLink(R.mipmap.dribbble, R.string.dribbble, url);
    }

    @NonNull
    public AboutBuilder addWebsiteLink(int url) {
        return addWebsiteLink(context.getString(url));
    }

    @NonNull
    public AboutBuilder addWebsiteLink(String url) {
        return addLink(R.mipmap.website, R.string.website, url);
    }

    @NonNull
    public AboutBuilder addEmailLink(int email, int subject, int message) {
        return addEmailLink(context.getString(email), context.getString(subject), context.getString(message));
    }

    @NonNull
    public AboutBuilder addEmailLink(int email, String subject, String message) {
        return addEmailLink(context.getString(email), subject, message);
    }

    @NonNull
    public AboutBuilder addEmailLink(int email, String subject) {
        return addEmailLink(context.getString(email), subject, null);
    }

    @NonNull
    public AboutBuilder addEmailLink(int email, int subject) {
        return addEmailLink(context.getString(email), context.getString(subject), null);
    }

    @NonNull
    public AboutBuilder addEmailLink(String email, String subject, String message) {
        return addLink(R.mipmap.email, R.string.email, util.sendEmail(email, subject, message));
    }

    @NonNull
    public AboutBuilder addEmailLink(int email) {
        return addEmailLink(context.getString(email));
    }

    @NonNull
    public AboutBuilder addEmailLink(String email) {
        return addLink(R.mipmap.email, R.string.email, util.sendEmail(email, null, null));
    }

    @NonNull
    public AboutBuilder addAction(Bitmap icon, String label, View.OnClickListener onClickListener) {
        actions.add(new Item(icon, label, onClickListener));
        return this;
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, String label, Intent intent) {
        return addAction(icon, label, util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, String label, Uri uri) {
        return addAction(icon, label, util.clickUri(uri));
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, String label, String url) {
        return addAction(icon, label, Uri.parse(url));
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, int label, View.OnClickListener onClickListener) {
        return addAction(icon, context.getString(label), onClickListener);
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, int label, Intent intent) {
        return addAction(icon, label, util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, int label, Uri uri) {
        return addAction(icon, label, util.clickUri(uri));
    }


    @NonNull
    public AboutBuilder addAction(Bitmap icon, int label, String url) {
        return addAction(icon, label, Uri.parse(url));
    }


    @NonNull
    public AboutBuilder addAction(int icon, int label, View.OnClickListener onClickListener) {
        return addAction(IconUtil.getBitmap(context, icon), context.getString(label), onClickListener);
    }


    @NonNull
    public AboutBuilder addAction(int icon, int label, Intent intent) {
        return addAction(icon, label, util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addAction(int icon, int label, Uri uri) {
        return addAction(icon, label, util.clickUri(uri));
    }


    @NonNull
    public AboutBuilder addAction(int icon, int label, String url) {
        return addAction(icon, label, Uri.parse(url));
    }


    @NonNull
    public AboutBuilder addAction(int icon, String label, View.OnClickListener onClickListener) {
        return addAction(IconUtil.getBitmap(context, icon), label, onClickListener);
    }


    @NonNull
    public AboutBuilder addAction(int icon, String label, Intent intent) {
        return addAction(icon, label, util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addAction(int icon, String label, Uri uri) {
        return addAction(icon, label, util.clickUri(uri));
    }


    @NonNull
    public AboutBuilder addAction(int icon, String label, String url) {
        return addAction(icon, label, Uri.parse(url));
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, int label, View.OnClickListener onClickListener) {
        return addAction(IconUtil.getBitmap(icon), context.getString(label), onClickListener);
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, int label, Intent intent) {
        return addAction(icon, label, util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, int label, Uri uri) {
        return addAction(icon, label, util.clickUri(uri));
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, int label, String url) {
        return addAction(icon, label, Uri.parse(url));
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, String label, View.OnClickListener onClickListener) {
        return addAction(IconUtil.getBitmap(icon), label, onClickListener);
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, String label, Intent intent) {
        return addAction(icon, label, util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, String label, Uri uri) {
        return addAction(icon, label, util.clickUri(uri));
    }


    @NonNull
    public AboutBuilder addAction(@NonNull BitmapDrawable icon, String label, String url) {
        return addAction(icon, label, Uri.parse(url));
    }


    @NonNull
    public AboutBuilder addFiveStarsAction(int appId) {
        return addFiveStarsAction(context.getString(appId));
    }


    @NonNull
    public AboutBuilder addFiveStarsAction(String appId) {
        return addAction(R.mipmap.star, R.string.rate_five_stars, util.openPlayStoreAppPage(appId));
    }


    @NonNull
    public AboutBuilder addFiveStarsAction() {
        return addFiveStarsAction(getApplicationID());
    }


    @NonNull
    public AboutBuilder addUpdateAction(int appId) {
        return addUpdateAction(context.getString(appId));
    }



    @NonNull
    public AboutBuilder addUpdateAction(String appId) {
        return addAction(R.mipmap.update, R.string.update_app, util.openPlayStoreAppPage(appId));
    }


    @NonNull
    public AboutBuilder addUpdateAction() {
        return addUpdateAction(getApplicationID());
    }


    @NonNull
    public AboutBuilder addMoreFromMeAction(int userName) {
        return addMoreFromMeAction(context.getString(userName));
    }


    @NonNull
    public AboutBuilder addMoreFromMeAction(String userName) {
        return addAction(R.mipmap.google_play_store, R.string.more_apps, util.openPlayStoreAppsList(userName));
    }


    @NonNull
    public AboutBuilder addShareAction(int subject, int message) {
        return addShareAction(context.getString(subject), context.getString(message));
    }


    @NonNull
    public AboutBuilder addShareAction(String subject, String message) {
        return addAction(share, R.string.share_app, util.shareThisApp(subject, message));
    }


    @NonNull
    public AboutBuilder addShareAction(String subject) {
        return addShareAction(subject, context.getString(R.string.uri_play_store_app_website, context.getPackageName()));
    }


    @NonNull
    public AboutBuilder addShareAction(int subject) {
        return addShareAction(context.getString(subject));
    }



    @NonNull
    public AboutBuilder addFeedbackAction(int email, int subject, int content) {
        return addFeedbackAction(context.getString(email), context.getString(subject), context.getString(content));
    }


    @NonNull
    public AboutBuilder addFeedbackAction(int email, String subject, String content) {
        return addAction(R.mipmap.feedback, R.string.feedback_app, util.sendEmail(context.getString(email), subject, content));
    }


    @NonNull
    public AboutBuilder addFeedbackAction(String email, String subject, String content) {
        return addAction(R.mipmap.feedback, R.string.feedback_app, util.sendEmail(email, subject, content));
    }


    @NonNull
    public AboutBuilder addFeedbackAction(int email, int subject) {
        return addFeedbackAction(context.getString(email), context.getString(subject));
    }


    @NonNull
    public AboutBuilder addFeedbackAction(String email, String subject) {
        return addFeedbackAction(email, subject, null);
    }


    @NonNull
    public AboutBuilder addFeedbackAction(int email, String subject) {
        return addFeedbackAction(context.getString(email), subject, null);
    }


    @NonNull
    public AboutBuilder addFeedbackAction(int email) {
        return addFeedbackAction(context.getString(email));
    }


    @NonNull
    public AboutBuilder addFeedbackAction(String email) {
        return addFeedbackAction(email, null);
    }


    @NonNull
    public AboutBuilder addIntroduceAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.intrduce, R.string.introduce_app, onClickListener);
    }


    @NonNull
    public AboutBuilder addIntroduceAction(Intent intent) {
        return addIntroduceAction(util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addHelpAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.help, R.string.help, onClickListener);
    }


    @NonNull
    public AboutBuilder addHelpAction(Intent intent) {
        return addHelpAction(util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addLicenseAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.license, R.string.license, onClickListener);
    }


    @NonNull
    public AboutBuilder addLicenseAction(Intent intent) {
        return addLicenseAction(util.clickIntent(intent));
    }


    @NonNull
    public AboutBuilder addChangeLogAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.changelog, R.string.changelog, onClickListener);
    }


    @NonNull
    public AboutBuilder addChangeLogAction(Intent intent) {
        return addChangeLogAction(util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addRemoveAdsAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.ads, R.string.remove_ads, onClickListener);
    }


    @NonNull
    public AboutBuilder addRemoveAdsAction(Intent intent) {
        return addRemoveAdsAction(util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder addDonateAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.donate, R.string.donate, onClickListener);
    }

    @NonNull
    public AboutBuilder addDonateAction(Intent intent) {
        return addDonateAction(util.clickIntent(intent));
    }

    @NonNull
    public AboutBuilder setShowAsCard(boolean showAsCard) {
        this.showAsCard = showAsCard;
        return this;
    }

    @NonNull
    public AboutBuilder addPrivacyPolicyAction(String url) {
        return addAction(R.mipmap.privacy, R.string.privacy, util.intent(url));
    }

    @NonNull
    public AboutBuilder addPrivacyPolicyAction(View.OnClickListener onClickListener) {
        return addAction(R.mipmap.privacy, R.string.privacy, onClickListener);
    }

    @NonNull
    public AboutBuilder addPrivacyPolicyAction(Intent intent) {
        return addAction(R.mipmap.privacy, R.string.privacy, util.clickIntent(intent));
    }

    public boolean isShowAsCard() {
        return showAsCard;
    }

    public String getName() {
        return name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getBrief() {
        return brief;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public Bitmap getCover() {
        return cover;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public int getNameColor() {
        return nameColor;
    }

    public int getSubTitleColor() {
        return subTitleColor;
    }

    public int getBriefColor() {
        return briefColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public int getIconColor() {
        return iconColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getLinksColumnsCount() {
        return linksColumnsCount;
    }

    public int getActionsColumnsCount() {
        return actionsColumnsCount;
    }

    public boolean isShowDivider() {
        return showDivider;
    }

    public int getDividerHeight() {
        return dividerHeight;
    }

    public int getDividerDashWidth() {
        return dividerDashWidth;
    }

    public int getDividerDashGap() {
        return dividerDashGap;
    }

    public boolean isLinksAnimated() {
        return linksAnimated;
    }

    public boolean isWrapScrollView() {
        return wrapScrollView;
    }

    @NonNull
    public LinkedList<Item> getLinks() {
        return links;
    }

    @NonNull
    public LinkedList<Item> getActions() {
        return actions;
    }

    @NonNull
    public AboutView build() {
        AboutView aboutView = new AboutView(context);
        aboutView.build(this);
        return aboutView;
    }

}
