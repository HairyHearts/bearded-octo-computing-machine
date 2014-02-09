package org.hairyhearts;

import android.app.Application;
import android.util.Log;
import com.baasbox.android.*;

import java.util.List;

public class HairyHearts extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BaasBox.Config config = new BaasBox.Config();
        config.authenticationType= BaasBox.Config.AuthType.SESSION_TOKEN;
        config.apiDomain = "baasbox-hairyhearts.rhcloud.com";
        config.httpPort=80;
        
        BaasBox.initDefault(this,config);
    }

}
