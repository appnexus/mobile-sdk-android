
/*
 *    Copyright 2022 XANDR INC
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

import java.util.Objects;

public class ANUserId {

    private String source;
    private String userId;

    public enum Source {
        CRITEO,
        THE_TRADE_DESK,
        NETID,
        LIVERAMP,
        UID2
    }

    public static final String EXTENDEDID_SOURCE_LIVERAMP = "liveramp.com";
    public static final String EXTENDEDID_SOURCE_NETID = "netid.de";
    public static final String EXTENDEDID_SOURCE_CRITEO = "criteo.com";
    public static final String EXTENDEDID_SOURCE_THETRADEDESK = "adserver.org";
    public static final String EXTENDEDID_SOURCE_UID2 = "uidapi.com";


    public ANUserId(String source, String userId) {
        this.userId = userId;
        this.source = source;
    }

    public ANUserId(ANUserId.Source source, String userId) {
        this.userId = userId;
        switch (source){
            case UID2:
                this.source=EXTENDEDID_SOURCE_UID2;
                break;
            case NETID:
                this.source=EXTENDEDID_SOURCE_NETID;
                break;
            case CRITEO:
                this.source=EXTENDEDID_SOURCE_CRITEO;
                break;
            case LIVERAMP:
                this.source=EXTENDEDID_SOURCE_LIVERAMP;
                break;
            case THE_TRADE_DESK:
                this.source=EXTENDEDID_SOURCE_THETRADEDESK;
                break;
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ANUserId anUserId = (ANUserId) o;
        return Objects.equals(source, anUserId.source) && Objects.equals(userId, anUserId.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, userId);
    }

    @Override
    public String toString() {
        // Maybe include RTI Partner in future (TBD)
        return "{\"source\":\"" + source + "\",\"id\":\"" + userId + "\"}";
    }
}
