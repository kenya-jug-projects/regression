package com.kenyajug;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.kenyajug.core.DateTimeManager;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;
public class DateTimeManagerTest {
    @Test
    public void shouldConvertDateTextToUTCTimestampTest(){
        var date = LocalDate.of(1999,4,21);
        var time = LocalTime.of(22,11,54);
        var expectedTimestamp = LocalDateTime.of(date,time);
        var dateText = "1999-04-21 22:11:54 UTC";
        var actualUTCDate = DateTimeManager.dateTextToUTCTimestamp(dateText);
        assertThat(actualUTCDate).isNotNull();
        assertThat(actualUTCDate.isEqual(expectedTimestamp));
    }
    @Test
    public void shouldConvertTimestampToUTCTextTest(){
        var date = LocalDate.of(1999,4,21);
        var time = LocalTime.of(22,11,54);
        var timestamp = LocalDateTime.of(date,time);
        var expectedTimeText = "1999-04-21 22:11:54 UTC";
        var actualTimestampText = DateTimeManager.timestampToUTCText(timestamp);
        assertThat(actualTimestampText).isNotNull();
        assertThat(actualTimestampText).isEqualTo(expectedTimeText);
    }
}
