package org.marid.runtime.model;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
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
import java.util.TimeZone;
import java.util.function.BiFunction;

public interface Literal extends ConstantArgument {

  Type getType();

  void setType(Type type);

  String getValue();

  void setValue(String value);

  @Override default String tag() {return "literal";}

  enum Type {

    VOID(void.class, (s, cl) -> null),
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
    URL(java.net.URL.class, (s, cl) -> {
      try {
        return new URL(s);
      } catch (MalformedURLException e) {
        throw new IllegalStateException(e);
      }
    }),
    INETADDRESS(InetAddress.class, (s, cl) -> {
      try {
        return InetAddress.getByName(s);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }),
    PATH(Path.class, (s, cl) -> Path.of(s)),
    FILE(File.class, (s, cl) -> new File(s)),
    CLASS(Class.class, (s, cl) -> {
      try {
        return cl.loadClass(s);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    }),
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
