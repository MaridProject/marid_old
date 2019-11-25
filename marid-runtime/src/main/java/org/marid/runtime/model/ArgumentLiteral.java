package org.marid.runtime.model;

/*-
 * #%L
 * marid-runtime
 * %%
 * Copyright (C) 2012 - 2019 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.jetbrains.annotations.NotNull;
import org.marid.io.function.IOBiFunction;
import org.marid.misc.ClassLoadingSupplier;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.File;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.BitSet;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.BiFunction;

public final class ArgumentLiteral extends ConstantArgument {

  private final Type type;
  private String value;

  public ArgumentLiteral(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  public ArgumentLiteral(@NotNull Element element) {
    super(element);
    type = Type.valueOf(element.getTagName().toUpperCase());
    value = element.getTextContent();
  }

  public ArgumentLiteral(@NotNull InputSource inputSource) {
    this(element(inputSource));
  }

  public Type getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public ArgumentLiteral setValue(String value) {
    this.value = value;
    return this;
  }

  @Override
  public @NotNull String getTag() {
    return type.name().toLowerCase();
  }

  @Override
  public void writeTo(@NotNull Element element) {
    element.setTextContent(value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ArgumentLiteral) {
      final var that = (ArgumentLiteral) obj;
      return Objects.equals(this.type, that.type)
          && Objects.equals(this.value, that.value);
    }
    return false;
  }

  public enum Type {

    BYTE(byte.class, (s, cl) -> Byte.parseByte(s)),
    SHORT(short.class, (s, cl) -> Short.parseShort(s)),
    INT(int.class, (s, cl) -> Integer.parseInt(s)),
    LONG(long.class, (s, cl) -> Long.parseLong(s)),
    FLOAT(float.class, (s, cl) -> Float.parseFloat(s)),
    DOUBLE(double.class, (s, cl) -> Double.parseDouble(s)),
    CHAR(char.class, (s, cl) -> s.charAt(0)),
    BOOLEAN(boolean.class, (s, cl) -> Boolean.parseBoolean(s)),
    STRING(String.class, (s, cl) -> s),
    BIGDECIMAL(BigDecimal.class, (s, cl) -> convertBigDecimal(s)),
    BIGINT(BigInteger.class, (s, cl) -> convertBigInt(s)),
    BITSET(BitSet.class, (s, cl) -> BitSet.valueOf(new BigInteger(s, 2).toByteArray())),
    UUID(java.util.UUID.class, (s, cl) -> java.util.UUID.fromString(s)),
    LOCALE(Locale.class, (s, cl) -> Locale.forLanguageTag(s)),
    CURRENCY(Currency.class, (s, cl) -> Currency.getInstance(s)),
    TIMEZONE(TimeZone.class, (s, cl) -> TimeZone.getTimeZone(s)),
    ZONEID(ZoneId.class, (s, cl) -> ZoneId.of(s)),
    TIMESTAMP(Timestamp.class, (s, cl) -> Timestamp.valueOf(s)),
    TIME(Time.class, (s, cl) -> Time.valueOf(s)),
    DATE(Date.class, (s, cl) -> Date.valueOf(s)),
    INSTANT(Instant.class, (s, cl) -> Instant.parse(s)),
    DURATION(Duration.class, (s, cl) -> Duration.parse(s)),
    LOCALDATE(LocalDate.class, (s, cl) -> LocalDate.parse(s)),
    LOCALTIME(LocalTime.class, (s, cl) -> LocalTime.parse(s)),
    LOCALDATETIME(LocalDateTime.class, (s, cl) -> LocalDateTime.parse(s)),
    PERIOD(Period.class, (s, cl) -> Period.parse(s)),
    MONTHDAY(MonthDay.class, (s, cl) -> MonthDay.parse(s)),
    YEAR(Year.class, (s, cl) -> Year.parse(s)),
    YEARMONTH(YearMonth.class, (s, cl) -> YearMonth.parse(s)),
    ZONEDDATETIME(ZonedDateTime.class, (s, cl) -> ZonedDateTime.parse(s)),
    ZONEOFFSET(ZoneOffset.class, (s, cl) -> ZoneOffset.of(s)),
    BASE64(byte[].class, (s, cl) -> Base64.getDecoder().decode(s)),
    BASE64URL(byte[].class, (s, cl) -> Base64.getUrlDecoder().decode(s)),
    BASE64MIME(byte[].class, (s, cl) -> Base64.getMimeDecoder().decode(s)),
    HEX(byte[].class, (s, cl) -> new BigInteger(s.replaceAll("\\s++", ""), 16).toByteArray()),
    URI(java.net.URI.class, (s, cl) -> java.net.URI.create(s)),
    URL(java.net.URL.class, IOBiFunction.of((s, cl) -> new URL(s))),
    INETADDRESS(InetAddress.class, IOBiFunction.of((s, cl) -> InetAddress.getByName(s))),
    PATH(Path.class, (s, cl) -> Path.of(s)),
    FILE(File.class, (s, cl) -> new File(s)),
    CLASS(Class.class, (s, cl) -> ClassLoadingSupplier.of(() -> cl.loadClass(s))),
    METHODTYPE(MethodType.class, MethodType::fromMethodDescriptorString);

    public final Class<?> type;
    public final BiFunction<@NotNull String, @NotNull ClassLoader, @NotNull Object> converter;

    Type(Class<?> type, BiFunction<String, ClassLoader, Object> converter) {
      this.type = type;
      this.converter = converter;
    }

    private static BigDecimal convertBigDecimal(String s) {
      switch (s) {
        case "0":
        case "0.0": return BigDecimal.ZERO;
        case "1":
        case "1.0": return BigDecimal.ONE;
        case "10":
        case "10.0": return BigDecimal.TEN;
        default: return new BigDecimal(s);
      }
    }

    private static BigInteger convertBigInt(String s) {
      switch (s) {
        case "0": return BigInteger.ZERO;
        case "1": return BigInteger.ONE;
        case "2": return BigInteger.TWO;
        case "10": return BigInteger.TEN;
        default: return new BigInteger(s);
      }
    }
  }
}
