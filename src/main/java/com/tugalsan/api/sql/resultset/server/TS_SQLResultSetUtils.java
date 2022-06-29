package com.tugalsan.api.sql.resultset.server;

import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.string.server.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.unsafe.client.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

public class TS_SQLResultSetUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLResultSetUtils.class.getSimpleName());

    public static class Meta {

        public static ResultSetMetaData get(ResultSet resultSet) {
            try {
                return resultSet.getMetaData();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Html {

        public static String table(ResultSet rs, int fontsizeHeader, int fontsizeData) {
            var sb = new StringBuilder();
            sb.append("<table>\n");
            sb.append(header(rs, fontsizeHeader));
            IntStream.range(0, Row.size(rs)).forEachOrdered(ri -> {
                Row.scrll(rs, ri);
                sb.append(row(rs, fontsizeData));
            });
            sb.append("</table>\n");
            return sb.toString();
        }

        private static String header(ResultSet rs, int fontsize) {
            var sb = new StringBuilder();
            sb.append("<tr>");
            IntStream.range(0, Row.size(rs)).forEachOrdered(i -> {
                sb.append(col(rs, fontsize, i));
            });
            sb.append("</tr>\n");
            return sb.toString();
        }

        private static String row(ResultSet rs, int fontsize) {
            StringBuilder sb = new StringBuilder();
            sb.append("<tr>");
            IntStream.range(0, Row.size(rs)).forEachOrdered(i -> {
                sb.append(col(rs, fontsize, i));
            });
            sb.append("</tr>\n");
            return sb.toString();
        }

        private static String col(ResultSet rs, int fontsize, int columnIndex) {
            return col(fontsize, Col.name(rs, columnIndex));
        }

        private static String col(int fontsize, CharSequence value) {
            var sb = new StringBuilder();
            sb.append("<td><font size=");
            sb.append(fontsize);
            sb.append(">");
            sb.append(value);
            sb.append("</font></td>");
            return sb.toString();
        }
    }

    public static class Col {

        public static boolean valid(int colIdx, int colSize) {
            var val = colIdx < colSize && colIdx >= 0;
            if (!val) {
                d.ce("Col.valid.false", colIdx);
            }
            return val;
        }

        public static boolean valid(ResultSet resultSet, CharSequence columnName) {
            var val = getIdx(resultSet, columnName) != -1;
            if (!val) {
                d.ce("Col.valid.false", columnName);
            }
            return val;
        }

        public static int getIdx(ResultSet resultSet, CharSequence columnName) {
            var cn = columnName.toString();
            var idx = cn.indexOf(".");
            var fcn = idx == -1 ? cn : cn.substring(idx + 1);
            var result = IntStream.range(0, size(resultSet))
                    .filter(ci -> Objects.equals(name(resultSet, ci), fcn)).findAny().orElse(-1);
            if (result == -1) {
                result = IntStream.range(0, size(resultSet))
                        .filter(ci -> Objects.equals(label(resultSet, ci), fcn)).findAny().orElse(-1);
            }
            return result;
        }

        public static boolean isEmpty(ResultSet resultSet) {
            return size(resultSet) == 0;
        }

        public static int size(ResultSet resultSet) {
            try {
                return Meta.get(resultSet).getColumnCount();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static String name(ResultSet resultSet, int colIdx) {
            try {
                return Meta.get(resultSet).getColumnName(colIdx + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static String label(ResultSet resultSet, int colIdx) {
            try {
                return Meta.get(resultSet).getColumnLabel(colIdx + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Row {

        public static boolean valid(int rowIndex, int rowSize) {
            var val = rowIndex < rowSize && rowIndex >= 0;
            if (!val) {
                d.ce("Row.valid.false", rowIndex);
            }
            return val;
        }

        public static void scrll(ResultSet resultSet, int ri) {
            try {
                if (ri < 0) {
                    return;
                }
                resultSet.absolute(ri + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static void scrllBottom(ResultSet resultSet) {
            try {
                resultSet.last();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static void scrllTop(ResultSet resultSet) {
            try {
                resultSet.first();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static int curIdx(ResultSet resultSet) {
            try {
                return resultSet.getRow() - 1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static boolean isEmpty(ResultSet resultSet) {
            return size(resultSet) == 0;
        }

        public static int size(ResultSet resultSet) {
            var backupIndex = curIdx(resultSet);
            scrllBottom(resultSet);
            var bottomIndex = curIdx(resultSet);
            scrll(resultSet, backupIndex);
            return bottomIndex + 1;
        }
    }

    public static class Obj {

        public static Object get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, columnName);
        }

        public static Object get(ResultSet resultSet, int rowIndex, int colIndex) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colIndex);
        }

        public static Object get(ResultSet resultSet, CharSequence columnName) {
            try {
                return resultSet.getObject(columnName.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static Object get(ResultSet resultSet, int colIdx) {
            try {
                return resultSet.getObject(colIdx + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BlobBytes {

        public static byte[] get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, columnName);
        }

        public static byte[] get(ResultSet resultSet, int rowIndex, int colIndex) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colIndex);
        }

        public static byte[] get(ResultSet resultSet, CharSequence columnName) {
            try {
                return resultSet.getBytes(columnName.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static byte[] get(ResultSet resultSet, int colIdx) {
            try {
                return resultSet.getBytes(colIdx + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BlobStr {

        public static String get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            var valBytes = BlobBytes.get(resultSet, rowIndex, columnName);
            if (valBytes == null) {
                return "";
            }
            return TS_StringUtils.toString(valBytes);
        }

        public static String get(ResultSet resultSet, int rowIndex, int colIndex) {
            var valBytes = BlobBytes.get(resultSet, rowIndex, colIndex);
            if (valBytes == null) {
                return "";
            }
            return TS_StringUtils.toString(valBytes);
        }

        public static String get(ResultSet resultSet, CharSequence columnName) {
            var valBytes = BlobBytes.get(resultSet, columnName);
            if (valBytes == null) {
                return "";
            }
            return TS_StringUtils.toString(valBytes);
        }

        public static String get(ResultSet resultSet, int colIdx) {
            var valBytes = BlobBytes.get(resultSet, colIdx);
            if (valBytes == null) {
                return "";
            }
            return TS_StringUtils.toString(valBytes);
        }
    }

    public static class Lng {

        public static long get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, columnName.toString());
        }

        public static long get(ResultSet resultSet, int rowIndex, int colIndex) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colIndex);
        }

        public static long get(ResultSet resultSet, int colIndex) {
            try {
                return resultSet.getLong(colIndex + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static long get(ResultSet resultSet, CharSequence columnName) {
            try {
                return resultSet.getLong(columnName.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class LngArr {

        public static List<Long> get(ResultSet rs, int colIndex) {
            List<Long> target = TGS_ListUtils.of();
            IntStream.range(0, Row.size(rs)).forEachOrdered(ri -> {
                target.add(Lng.get(rs, ri, colIndex));
            });
            return target;
        }

        public static List<Long> get(ResultSet rs, CharSequence columnName) {
            List<Long> target = TGS_ListUtils.of();
            IntStream.range(0, Row.size(rs)).forEachOrdered(ri -> {
                target.add(Lng.get(rs, ri, columnName.toString()));
            });
            return target;
        }
    }

    public static class Str {

        public static String get(ResultSet resultSet, int ri, int ci) {
            Row.scrll(resultSet, ri);
            return get(resultSet, ci);
        }

        public static String get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, columnName);
        }

        public static String get(ResultSet resultSet, int ci) {
            return TGS_UnSafe.compile(() -> resultSet.getString(ci + 1));
        }

        public static String get(ResultSet resultSet, CharSequence columnName) {
            return TGS_UnSafe.compile(() -> resultSet.getString(columnName.toString()));
        }
    }

    public static class StrArr {

        public static List<String> get(ResultSet rs, int ci) {
            List<String> target = TGS_ListUtils.of();
            IntStream.range(0, Row.size(rs)).forEachOrdered(ri -> {
                var vi = Str.get(rs, ri, ci);
                d.ci("StrArr.get", "ri", ri, "ci", ci, "vi", vi);
                target.add(vi);
            });
            return target;
        }

        public static List<String> get(ResultSet rs, CharSequence columnName) {
            List<String> target = TGS_ListUtils.of();
            IntStream.range(0, Row.size(rs)).forEachOrdered(ri -> {
                target.add(Str.get(rs, ri, columnName.toString()));
            });
            return target;
        }
    }

    public static class Date {

        public static TGS_Time get(ResultSet resultSet, int rowIndex, CharSequence colName) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colName);
        }

        public static TGS_Time get(ResultSet resultSet, int rowIndex, int colIdx) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colIdx);
        }

        public static TGS_Time get(ResultSet resultSet, CharSequence colName) {
            var val = Lng.get(resultSet, colName);
            return TGS_Time.toDate(val);
        }

        public static TGS_Time get(ResultSet resultSet, int colIdx) {
            var val = Lng.get(resultSet, colIdx);
            return TGS_Time.toDate(val);
        }
    }

    public static class Time {

        public static TGS_Time get(ResultSet resultSet, int rowIndex, CharSequence colName) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colName);
        }

        public static TGS_Time get(ResultSet resultSet, int rowIndex, int colIdx) {
            Row.scrll(resultSet, rowIndex);
            return get(resultSet, colIdx);
        }

        public static TGS_Time get(ResultSet resultSet, CharSequence colName) {
            var val = Lng.get(resultSet, colName);
            return TGS_Time.toTime(val);
        }

        public static TGS_Time get(ResultSet resultSet, int colIdx) {
            var val = Lng.get(resultSet, colIdx);
            return TGS_Time.toTime(val);
        }
    }
}
