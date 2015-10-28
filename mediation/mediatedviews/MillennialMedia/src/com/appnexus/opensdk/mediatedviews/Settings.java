package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.util.Pair;

import com.appnexus.opensdk.TargetingParameters;
import com.appnexus.opensdk.utils.StringUtil;
import com.millennialmedia.UserData;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class Settings {

    static UserData getUserData(final TargetingParameters tp, final Activity activity) {
        final UserData userData = new UserData();
        if (tp != null) {
            switch (tp.getGender()) {
                case UNKNOWN:
                    userData.setGender(UserData.Gender.UNKNOWN);
                    break;
                case FEMALE:
                    userData.setGender(UserData.Gender.FEMALE);
                    break;
                case MALE:
                    userData.setGender(UserData.Gender.MALE);
                    break;
            }

            if (tp.getAge() != null) {
                try {
                    String age = tp.getAge();
                    if (age.contains("-")) {
                        int dash = age.indexOf("-");
                        int age1 = Integer.parseInt(age.substring(0, dash));
                        int age2 = Integer.parseInt(age.substring(dash + 1));
                        userData.setAge((age1 + age2) / 2);
                    } else {
                        int age_int = Integer.parseInt(tp.getAge());
                        if (age_int > 1900) {
                            GregorianCalendar calendar = new GregorianCalendar();
                            Date date = new Date();
                            calendar.setTime(date);
                            int year = calendar.get(Calendar.YEAR);
                            age_int = year - age_int;
                        }
                        if (age_int > 0) {
                            userData.setAge(age_int);
                        }
                    }
                } catch (NumberFormatException e) {
                } catch (IllegalArgumentException e1) {
                } catch (ArrayIndexOutOfBoundsException e2) {
                }
            }
            if (tp.getCustomKeywords() != null) {
                String keywords = "";
                for (Pair<String, String> p : tp.getCustomKeywords()) {
                    if (!StringUtil.isEmpty(p.first) && !StringUtil.isEmpty(p.second)) {
                        if (keywords.length() == 0) {
                            keywords = p.first + ":" + p.second;
                        } else {
                            keywords = keywords + "," + p.first + ":" + p.second;
                        }

                    }
                    userData.setKeywords(keywords);
                }
            }

        }
        return userData;
    }
}
