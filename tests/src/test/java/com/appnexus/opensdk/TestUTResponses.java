/*
 *    Copyright 2013 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk;

import com.appnexus.opensdk.util.TestUtil;

import java.util.ArrayList;

/**
 * Used for mock responses
 */
public class TestUTResponses {

    public static final String RESULTCB = "http://result.com/";

    // template strings
    private static final String CLASSNAME = "com.appnexus.opensdk.testviews.%s";

    // impbus response

    private static final String UT_RESPONSE = "{\"version\":\"0.0.2\",\"tags\":[{\"uuid\":\"null\",\"auction_id\":\"5049928823070184480\",\"ut_url\":\"\",\"tag_id\":5778861,\"ads\":[{\"content_source\":\"rtb\",\"ad_type\":\"video\",\"buyer_member_id\":3535,\"creative_id\":37357778,\"media_type_id\":4,\"media_subtype_id\":64,\"notify_url\":\"\",\"rtb\":{\"video\":{\"player_width\":0,\"duration_ms\":142000,\"playback_methods\":[\"auto_play_sound_off\"],\"frameworks\":[],\"content\":\"%s\"}}}]}]}";
    private static final String VAST_XML_RESPONSE = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><VAST version=\\\"2.0\\\"><Ad id=\\\"37357778\\\"><Wrapper><AdSystem version=\\\"2.0\\\">adnxs</AdSystem><VASTAdTagURI><![CDATA[/]]></VASTAdTagURI><Error><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=4&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Error><Impression id=\\\"adnxs\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=9&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Impression><Creatives><Creative id=\\\"9408\\\"><Linear><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=2&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=5&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=6&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=7&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://nym1.ib.adnxs.com/vast_track?e=wqT_3QKcBPBCEwIAAAIA1gAFCNmV6rEFEKCo68Ck8LyKRhjwpr3Ju4qAuhEgASotCXsUrkfheoQ_EXsUrkfheoQ_GQAAAAAAAPA_IRESACkRCagwrdvgAjjPG0DPG0gCUNKR6BFY2oYzYABo5MMBeLTQAoABAYoBA1VTRJIBAQbwUpgBwAKgAeADqAEBsAEAuAEAwAEDyAEA0AEA2AEA4AEA8AEAigI6dWYoJ2EnLCA2NTI4ODcsIDE0NDY2NzcyMDkpO3VmKCdyJywgMzczNTc3NzgsMh4A8LSSAqUBIU5pZk8td2piMDU0RkVOS1I2QkVZQUNEYWhqTXdBRGdBUUFSSXp4dFFyZHZnQWxnQVlGaG9BSEFBZUFDQUFRQ0lBUUNRQVFHWUFRR2dBUUdvQVFPd0FRQzVBWHNVcmtmaGVvUV93UUY3Rks1SDRYcUVQOGtCX1p2clU1SmdfVF9aQVFBQUFBQUFBUEFfNEFFQTlRRUFBQUFBbUFLS2lNeUVEZy4umgIdIWxRYXNQZ2piLqgA8Hcyb1l6SUFRLtgCAOACpKsrgAMAiAMBkAMAmAMXoAMBqgMAsAMAuAMAwAOQHMgDANIDKAgAEiRhNjVjMTExYy1kMjI4LTQ2OTctODAxZC0wYTMyODI1OGEwYzbYAwDgAwDoAwDwAwD4AwCABACSBAYvdXQvdjGYBAA.&event=8&s=09b6f53d34cc1ac313608a19d6920de34f1140f8]]></Tracking></TrackingEvents><VideoClicks><ClickTracking id=\\\"adnxs\\\"><![CDATA[http://nym1.ib.adnxs.com/click?exSuR-F6hD97FK5H4XqEPwAAAAAAAPA_exSuR-F6hD97FK5H4XqEPyDUGkiC8xRGcFMvuVMAdBHZijpWAAAAAK0tWADPDQAAzw0AAAIAAADSCDoCWsMMAAAAAQBVU0QAVVNEAEAB4AHkYQAANKgBAAMAAQAAAKYARSJsoQAAAAA./cnd=%21lQasPgjb054FENKR6BEY2oYzIAQ./]]></ClickTracking></VideoClicks></Linear></Creative></Creatives></Wrapper></Ad></VAST>";
    private static final String VAST_INLINE_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "\t  xsi:noNamespaceSchemaLocation=\"vast2.xsd\" version=\"2.0\"><Ad id=\"Customized\"><InLine><AdSystem>Open AdStream</AdSystem><AdTitle>Customized</AdTitle><Description>ad Description</Description><Impression><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ieg/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?]]></Impression><Creatives><Creative><Linear><Duration>00:01:01</Duration><TrackingEvents><Tracking event=\"start\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=start&XE]]></Tracking><Tracking event=\"midpoint\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=midpoint&XE]]></Tracking><Tracking event=\"firstQuartile\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=firstQuartile&XE]]></Tracking><Tracking event=\"thirdQuartile\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=thirdQuartile&XE]]></Tracking><Tracking event=\"complete\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=complete&XE]]></Tracking><Tracking event=\"mute\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=mute&XE]]></Tracking><Tracking event=\"pause\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=pause&XE]]></Tracking><Tracking event=\"fullscreen\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=fullscreen&XE]]></Tracking><Tracking event=\"rewind\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=rewind&XE]]></Tracking><Tracking event=\"unmute\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=unmute&XE]]></Tracking><Tracking event=\"expand\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=expand&XE]]></Tracking><Tracking event=\"collapse\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=collapse&XE]]></Tracking><Tracking event=\"acceptInvitation\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=acceptInvitation&XE]]></Tracking><Tracking event=\"close\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=close&XE]]></Tracking><Tracking event=\"resume\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=resume&XE]]></Tracking><Tracking event=\"creativeView\"><![CDATA[http://oasc18.247realmedia.com/t/video-demo.appnexus.com/outstream_1/L35/1298238887@x01/ld/L35/TJM_Imp/demo_outstream_1/creative_3_vast_x01/574e6d6d2b46574d4d3077414270354f?XE&oas_iactId=creativeView&XE]]></Tracking></TrackingEvents><VideoClicks><ClickThrough><![CDATA[http://oasc18.247realmedia.com/5c/video-demo.appnexus.com/outstream_1/L35/1298238887/x01/TJM_Imp/demo_outstream_1/creative_3_vast_x01.xml/574e6d6d2b46574d4d3077414270354f?]]></ClickThrough></VideoClicks><MediaFiles><MediaFile delivery=\"progressive\" bitrate=\"1049\" width=\"854\" height=\"480\" type=\"video/mp4\"><![CDATA[http://imagec18.247realmedia.com/0/TJM_Imp/demo_outstream_1/524D5F5641535431_creative_3_vast_x01.mp4/1446478842]]></MediaFile></MediaFiles></Linear></Creative></Creatives></InLine></Ad></VAST>";
    private static final String RESPONSE = "{\"status\":\"%s\",\"ads\":%s,\"mediated\":%s,\"native\":%s,\"version\":%d}";
    private static final String ADS = "[{ \"type\": \"%s\", \"width\": %d, \"height\": %d, \"content\": \"%s\" }]";
    private static final String MEDIATED_AD = "{\"type\":\"%s\",\"class\":\"%s\",\"param\":\"%s\",\"width\":\"%d\",\"height\":\"%d\",\"id\":\"%s\"}";
    private static final String MEDIATED_ARRAY_SINGLE_AD = "[{ \"handler\": [{\"type\":\"%s\",\"class\":\"%s\",\"param\":\"%s\",\"width\":\"%d\",\"height\":\"%d\",\"id\":\"%s\"}],\"result_cb\":\"%s\"}]";
    private static final String HANDLER = "{ \"handler\": [%s],\"result_cb\":\"%s\"}";
    private static final String HANDLER_NO_RESULTCB = "{ \"handler\": [%s]}";
    private static final String MEDIATED_ARRAY = "[%s]";

    private static final String MRAID_CONTENT = "<script type=\\\"text/javascript\\\" src=\\\"mraid.js\\\"></script><script type=\\\"text/javascript\\\">document.write('<div style=\\\"background-color:#EF8200;height:1000px;width:1000px;\\\"><p>%s</p></div>');</script>";

    private static final String AN_NATIVE_RESPONSE = "[{\"type\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"full_text\":\"%s\",\"context\":\"%s\",\"icon_img_url\":\"%s\",\"main_media\":%s,\"cta\":\"%s\",\"click_trackers\":[%s],\"impression_trackers\":[%s],\"rating\":%s,\"click_url\":\"%s\",\"click_fallback_url\":\"%s\",\"custom\":%s}]";
    private static final String NATIVE_MAIN_MEDIA = "[{\"url\":\"%s\",\"width\":%d,\"height\":%d,\"label\":\"default\"},{\"url\":\"%s\",\"width\":%d,\"height\":%d},{\"url\":\"%s\",\"width\":%d,\"height\":%d}]";
    private static final String NATIVE_RATING = "{\"value\":%.2f,\"scale\":%.2f}";
    private static final String EMPTY_ARRAY = "[]";
    private static final String STATUS_OK = "ok";
    private static final String STATUS_NO_BID = "no_bid";

    public static String blank() {
        return "";
    }

    public static String banner() {
        return templateAdsResponse("banner", 320, 50, "content");
    }

    public static String video(){
        return templateUTVideoResponse(STATUS_OK, VAST_XML_RESPONSE);
    }

    public static String getVastInlineResponse(){
        return VAST_INLINE_RESPONSE;
    }

    public static String blankBanner() {
        return templateAdsResponse("banner", 320, 50, "");
    }

    public static String mraidBanner(String name) {
        return mraidBanner(name, 320, 50);
    }

    public static String mraidBanner(String name, int width, int height) {
        String content = String.format(MRAID_CONTENT, name);
        return templateAdsResponse("banner", width, height, content);
    }

    public static String mediatedSuccessfulBanner() {
        return templateSingleMediatedAdResponse(createClassName("SuccessfulBanner"), RESULTCB);
    }

    public static String mediatedFakeClass() {
        return templateSingleMediatedAdResponse(createClassName("FakeClass"), RESULTCB);
    }

    public static String mediatedDummyClass() {
        return templateSingleMediatedAdResponse(createClassName("DummyClass"), RESULTCB);
    }

    public static String mediatedNoRequest() {
        return templateSingleMediatedAdResponse(createClassName("NoRequestBannerView"), RESULTCB);
    }

    public static String mediatedOutOfMemory() {
        return templateSingleMediatedAdResponse(createClassName("OOMBannerView"), RESULTCB);
    }

    public static String mediatedNoFill() {
        return templateSingleMediatedAdResponse(createClassName("NoFillView"), RESULTCB);
    }

    public static String createClassName(String className) {
        return String.format(CLASSNAME, className);
    }

    public static String resultCB(int code) {
        return String.format(RESULTCB + "&reason=%d", code);
    }

    public static String waterfall(String[] classNames, String[] resultCBs) {
        if (classNames.length != resultCBs.length) {
            System.err.println("different numbers of class names and resultCBs");
            return "";
        }

        ArrayList<String> handlers = new ArrayList<String>(classNames.length);

        for (int i = 0; i < classNames.length; i++) {
            String[] mediatedAds = {templateMediatedAd(createClassName(classNames[i]))};
            String handler = templateHandlerFromMediatedAds(mediatedAds, resultCBs[i]);
            handlers.add(handler);
        }

        return templateMediatedResponseFromHandlers(handlers.toArray(new String[handlers.size()]));
    }

    public static String callbacks(int testNumber) {
        return templateSingleMediatedAdResponse("android", createClassName("CallbacksTestView"), "",
                320, 50, String.valueOf(testNumber), RESULTCB);
    }

    public static String templateUTVideoResponse(String status, String vastAd) {
        return String.format(UT_RESPONSE, vastAd);
    }

    // templates

    public static String templateResponse(String status, String ads, String mediated, String nativeResponse, int version) {
        return String.format(RESPONSE, status, ads, mediated, nativeResponse, version);
    }

    public static String templateAdsResponse(String type, int width, int height, String content) {
        String ads = String.format(ADS, type, width, height, content);
        return templateResponse(STATUS_OK, ads, EMPTY_ARRAY, EMPTY_ARRAY, TestUtil.VERSION);
    }

    public static String templateSingleMediatedAdResponse(String className, String resultCB) {
        return templateSingleMediatedAdResponse("android", className, "", 320, 50, "", resultCB);
    }

    public static String templateSingleMediatedAdResponse(String type, String className, String param, int width, int height, String id, String resultCB) {
        String mediatedAd = String.format(MEDIATED_ARRAY_SINGLE_AD, type, className, param, width, height, id, resultCB);
        return templateResponse(STATUS_OK, EMPTY_ARRAY, mediatedAd, EMPTY_ARRAY, TestUtil.VERSION);
    }

    public static String templateMediatedAd(String className) {
        return templateMediatedAd("android", className, "", 320, 50, "");
    }

    public static String templateMediatedAd(String type, String className, String param, int width, int height, String id) {
        return String.format(MEDIATED_AD, type, className, param, width, height, id);
    }

    public static String templateHandlerFromMediatedAds(String[] mediatedAds, String resultCB) {
        StringBuilder sb = new StringBuilder();
        for (String mediatedAd : mediatedAds) {
            sb.append(mediatedAd).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        if (resultCB == null) return String.format(HANDLER_NO_RESULTCB, sb.toString());
        return String.format(HANDLER, sb.toString(), resultCB);
    }

    public static String templateMediatedResponseFromHandlers(String[] handlers) {
        StringBuilder sb = new StringBuilder();
        for (String handler : handlers) {
            sb.append(handler).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return templateResponse(STATUS_OK, EMPTY_ARRAY, String.format(MEDIATED_ARRAY, sb.toString()), null, TestUtil.VERSION);
    }

    public static String templateNativeResponse(String type, String title, String description, String full_text, String context,
                                                String icon, String main_media, String cta, String click_trackers,
                                                String imp_trackers, String rating, String click_url,
                                                String click_fallback_url, String custom) {
        return String.format(AN_NATIVE_RESPONSE, type, title, description, full_text, context, icon, main_media, cta, click_trackers, imp_trackers, rating, click_url, click_fallback_url, custom);


    }

    public static String templateNativeMainMedia(String url, int width, int height, String url2, int width2, int height2, String url3, int width3, int height3) {
        return String.format(NATIVE_MAIN_MEDIA, url, width, height, url2, width2, height2, url3, width3, height3);
    }

    public static String templateNativeRating(float value, float scale) {
        return String.format(NATIVE_RATING, value, scale);
    }

    public static String anNative() {
        String nativeResponse = templateNativeResponse("native", "test title", "test description", "full text", "newsfeed",
                "http://path_to_icon.com", templateNativeMainMedia("http://path_to_main.com", 300, 200, "http://path_to_main2.com", 50, 50, "http://path_to_main3.com", 250, 250),
                "install", "\"http://ib.adnxs.com/click...\"", "\"http://ib.adnxs.com/it...\"", templateNativeRating(4f, 5f), "http://www.appnexus.com", "http://www.google.com", "{\"key\":\"value\"}"
        );
        return templateResponse(STATUS_OK, EMPTY_ARRAY, EMPTY_ARRAY, nativeResponse, TestUtil.VERSION);
    }

}
