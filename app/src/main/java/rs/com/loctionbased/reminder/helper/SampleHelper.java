package rs.com.loctionbased.reminder.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.FrameLayout;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.builder.AboutBuilder;
import rs.com.loctionbased.reminder.views.AboutView;

public class SampleHelper {

    private Activity activity;
    private int theme = R.style.AppThemeDark;

    private SampleHelper(Activity activity) {
        this.activity = activity;
    }

    public static SampleHelper with(Activity activity) {
        return new SampleHelper(activity);
    }

    public SampleHelper init() {
        activity.setTheme(theme);

        return this;
    }

    public void loadAbout() {
        final FrameLayout flHolder = activity.findViewById(R.id.about);


        SpannableStringBuilder str = new SpannableStringBuilder("Email: ");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AboutBuilder builder = AboutBuilder.with(activity)
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setPhoto(R.drawable.razasaeed)
                .setCover(R.mipmap.profile_cover)
                .setLinksAnimated(true)
                .setDividerDashGap(13)
                .setName("Muhammad Raza Saeed")
                .setSubTitle("Mobile Developer")
                .setLinksColumnsCount(4)
                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .addGooglePlayStoreLink("https://play.google.com/store/apps/developer?id=Muhammad+Raza+Saeed")
                .addGitHubLink("RazaSaeed")
                .addBitbucketLink("RazaSaeed")
                .addFacebookLink("https://web.facebook.com/AndroidDeveloperRaza")
                .addTwitterLink("https://twitter.com/MrRazaSaeed")
                .addInstagramLink("jnrvans")
                .addGooglePlusLink("+RazaSaeed")
                .addYoutubeChannelLink("user")
                .addDribbbleLink("user")
                .addLinkedInLink("https://www.linkedin.com/in/raza-saeed-13485a8a/")
                .addEmailLink("razasaeed135@gmail.com")
                .addWhatsappLink("Muhammad Raza Saeed", "+923165994525")
                .addSkypeLink("user")
                .addGoogleLink("user")
                .addAndroidLink("user")
                .addWebsiteLink("site")
                .addFiveStarsAction("https://play.google.com/store/apps/details?id=rs.com.razasaeed.memorizeme")
                .addMoreFromMeAction("Muhammad Raza Saeed")
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .addUpdateAction()
                .setActionsColumnsCount(2)
                .addFeedbackAction("razasaeed135@gmail.com")
                .addPrivacyPolicyAction("user")
                .addIntroduceAction((Intent) null)
                .addHelpAction((Intent) null)
                .addChangeLogAction((Intent) null)
                .addRemoveAdsAction((Intent) null)
                .addDonateAction((Intent) null)
                .setWrapScrollView(true)
                .setShowAsCard(true);

        AboutView view = builder.build();

        flHolder.addView(view);

    }
}
