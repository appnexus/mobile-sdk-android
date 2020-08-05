/*
 *    Copyright 2020 APPNEXUS INC
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class ResultCodeTest {

    @Test
    public void testResultCodeShouldNotOverrideMessage() {
        ResultCode code;
        String msg = "AdError::::::: CUSTOM_ADAPTER_ERROR22";
        code = ResultCode.getNewInstance(ResultCode.CUSTOM_ADAPTER_ERROR);
        code.setMessage(msg);
        localScope();
        assertEquals(msg, code.getMessage());
    }

    @Test
    public void testResultCodeEquals() {
        ResultCode code1 = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
        ResultCode code2 = ResultCode.getNewInstance(ResultCode.INTERNAL_ERROR);
        ResultCode code3 = ResultCode.getNewInstance(ResultCode.UNABLE_TO_FILL);
        assertTrue(code1.equals(code2));
        assertFalse(code1.equals(code3));

        assertFalse(code1 == code2);
        assertFalse(code1 == code3);

        assertTrue(code1.equals(ResultCode.INTERNAL_ERROR));
        assertTrue(code2.equals(ResultCode.INTERNAL_ERROR));
        assertTrue(code3.equals(ResultCode.UNABLE_TO_FILL));

        assertFalse(code1.equals(ResultCode.MEDIATED_SDK_UNAVAILABLE));
    }

    void localScope(){
        ResultCode code;
        String msg = "AdError:localscope INTERNAL_ERROR";
        code = ResultCode.getNewInstance(ResultCode.CUSTOM_ADAPTER_ERROR);
        code.setMessage(msg);
        assertEquals(msg, code.getMessage());
    }

}
