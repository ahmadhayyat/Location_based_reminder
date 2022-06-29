package rs.com.loctionbased.reminder.app.interfaces;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

public interface ViewHolderClickListener {
    public void onItemClicked(int position, @Nullable Intent optionalIntent, @Nullable Bundle optionalBundle);
    public boolean onItemLongClicked(int position);
}
