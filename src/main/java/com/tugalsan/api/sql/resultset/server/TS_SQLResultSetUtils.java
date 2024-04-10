package com.tugalsan.api.sql.resultset.server;

import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.string.server.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

public class TS_SQLResultSetUtils {

    final private static TS_Log d = TS_Log.of(TS_SQLResultSetUtils.class);

    public static class Meta {

        public static TGS_UnionExcuse<ResultSetMetaData> get(ResultSet resultSet) {
            try {
                return TGS_UnionExcuse.of(resultSet.getMetaData());
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }
    }

    public static class Html {

        public static TGS_UnionExcuse<String> table(ResultSet rs, int fontsizeHeader, int fontsizeData) {
            var sb = new StringBuilder();
            sb.append("<table>\n");
            sb.append(header(rs, fontsizeHeader));
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            for (var ri = 0; ri < u_rowSize.value(); ri++) {
                var u_scroll = Row.scrll(rs, ri);
                if (u_scroll.isExcuse()) {
                    return u_scroll.toExcuse();
                }
                sb.append(row(rs, fontsizeData));
            }
            sb.append("</table>\n");
            return TGS_UnionExcuse.of(sb.toString());
        }

        private static TGS_UnionExcuse<String> header(ResultSet rs, int fontsize) {
            var sb = new StringBuilder();
            sb.append("<tr>");
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            var rowSize = u_rowSize.value();
            IntStream.range(0, rowSize).forEachOrdered(i -> {
                sb.append(col(rs, fontsize, i));
            });
            sb.append("</tr>\n");
            return TGS_UnionExcuse.of(sb.toString());
        }

        private static TGS_UnionExcuse<String> row(ResultSet rs, int fontsize) {
            var sb = new StringBuilder();
            sb.append("<tr>");
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            var rowSize = u_rowSize.value();
            IntStream.range(0, rowSize).forEachOrdered(i -> {
                sb.append(col(rs, fontsize, i));
            });
            sb.append("</tr>\n");
            return TGS_UnionExcuse.of(sb.toString());
        }

        private static TGS_UnionExcuse<String> col(ResultSet rs, int fontsize, int columnIndex) {
            var u = Col.name(rs, columnIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return TGS_UnionExcuse.of(col(fontsize, u.value()));
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

        public static TGS_UnionExcuseVoid valid(ResultSet resultSet, CharSequence columnName) {
            var u = getIdx(resultSet, columnName);
            if (u.isExcuse()) {
                return TGS_UnionExcuseVoid.ofExcuse(u.excuse());
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }

        public static TGS_UnionExcuse<Integer> getIdx(ResultSet resultSet, CharSequence columnName) {
            var cn = columnName.toString();
            var idx = cn.indexOf(".");
            var fcn = idx == -1 ? cn : cn.substring(idx + 1);
            var u = size(resultSet);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            var result = IntStream.range(0, u.value())
                    .filter(ci -> Objects.equals(name(resultSet, ci), fcn)).findAny().orElse(-1);
            if (result == -1) {
                result = IntStream.range(0, u.value())
                        .filter(ci -> Objects.equals(label(resultSet, ci), fcn)).findAny().orElse(-1);
            }
            if (result == -1) {
                return TGS_UnionExcuse.ofExcuse(d.className, "getIdx", "result is -1");
            }
            return TGS_UnionExcuse.of(result);
        }

        public static TGS_UnionExcuse<Boolean> isEmpty(ResultSet resultSet) {
            var u = size(resultSet);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return TGS_UnionExcuse.of(u.value() == 0);
        }

        public static TGS_UnionExcuse<Integer> size(ResultSet resultSet) {
            var u = Meta.get(resultSet);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            try {
                return TGS_UnionExcuse.of(u.value().getColumnCount());
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<String> name(ResultSet resultSet, int colIdx) {
            var u = Meta.get(resultSet);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            try {
                return TGS_UnionExcuse.of(u.value().getColumnName(colIdx + 1));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<String> label(ResultSet resultSet, int colIdx) {
            var u = Meta.get(resultSet);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            try {
                return TGS_UnionExcuse.of(u.value().getColumnLabel(colIdx + 1));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
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

        public static TGS_UnionExcuseVoid scrll(ResultSet resultSet, int ri) {
            try {
                if (ri < 0) {
                    return TGS_UnionExcuseVoid.ofExcuse(new IllegalArgumentException("ri < 0 -> ri:" + ri));
                }
                resultSet.absolute(ri + 1);
                return TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException ex) {
                return TGS_UnionExcuseVoid.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuseVoid scrllBottom(ResultSet resultSet) {
            try {
                resultSet.last();
                return TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException ex) {
                return TGS_UnionExcuseVoid.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuseVoid scrllTop(ResultSet resultSet) {
            try {
                resultSet.first();
                return TGS_UnionExcuseVoid.ofVoid();
            } catch (SQLException ex) {
                return TGS_UnionExcuseVoid.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<Integer> curIdx(ResultSet resultSet) {
            try {
                return TGS_UnionExcuse.of(resultSet.getRow() - 1);
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<Boolean> isEmpty(ResultSet resultSet) {
            var u_size = size(resultSet);
            if (u_size.isExcuse()) {
                return u_size.toExcuse();
            }
            return TGS_UnionExcuse.of(u_size.value() == 0);
        }

        public static TGS_UnionExcuse<Integer> size(ResultSet resultSet) {
            var u_backupIndex = curIdx(resultSet);
            if (u_backupIndex.isExcuse()) {
                return u_backupIndex.toExcuse();
            }
            var backupIndex = u_backupIndex.value();
            var u_scrllBottom = scrllBottom(resultSet);
            if (u_scrllBottom.isExcuse()) {
                return u_scrllBottom.toExcuse();
            }
            var u_bottomIndex = curIdx(resultSet);
            if (u_bottomIndex.isExcuse()) {
                return u_bottomIndex.toExcuse();
            }
            var bottomIndex = u_bottomIndex.value();
            var u_scrll = scrll(resultSet, backupIndex);
            if (u_scrll.isExcuse()) {
                return u_scrll.toExcuse();
            }
            return TGS_UnionExcuse.of(bottomIndex + 1);
        }
    }

    public static class Obj {

        public static TGS_UnionExcuse<Object> get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, columnName);
        }

        public static TGS_UnionExcuse<Object> get(ResultSet resultSet, int rowIndex, int colIndex) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, colIndex);
        }

        public static TGS_UnionExcuse<Object> get(ResultSet resultSet, CharSequence columnName) {
            try {
                return TGS_UnionExcuse.of(resultSet.getObject(columnName.toString()));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<Object> get(ResultSet resultSet, int colIdx) {
            try {
                return TGS_UnionExcuse.of(resultSet.getObject(colIdx + 1));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }
    }

    public static class BlobBytes {

        public static TGS_UnionExcuse<byte[]> get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, columnName);
        }

        public static TGS_UnionExcuse<byte[]> get(ResultSet resultSet, int rowIndex, int colIndex) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, colIndex);
        }

        public static TGS_UnionExcuse<byte[]> get(ResultSet resultSet, CharSequence columnName) {
            try {
                return TGS_UnionExcuse.of(resultSet.getBytes(columnName.toString()));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<byte[]> get(ResultSet resultSet, int colIdx) {
            try {
                return TGS_UnionExcuse.of(resultSet.getBytes(colIdx + 1));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }
    }

    public static class BlobStr {

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            var u_valBytes = BlobBytes.get(resultSet, rowIndex, columnName);
            if (u_valBytes.isExcuse()) {
                return u_valBytes.toExcuse();
            }
            var valBytes = u_valBytes.value();
            if (valBytes == null) {
                return TGS_UnionExcuse.of("");
            }
            return TGS_UnionExcuse.of(TS_StringUtils.toString(valBytes));
        }

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, int rowIndex, int colIndex) {
            var u_valBytes = BlobBytes.get(resultSet, rowIndex, colIndex);
            if (u_valBytes.isExcuse()) {
                return u_valBytes.toExcuse();
            }
            var valBytes = u_valBytes.value();
            if (valBytes == null) {
                return TGS_UnionExcuse.of("");
            }
            return TGS_UnionExcuse.of(TS_StringUtils.toString(valBytes));
        }

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, CharSequence columnName) {
            var u_valBytes = BlobBytes.get(resultSet, columnName);
            if (u_valBytes.isExcuse()) {
                return u_valBytes.toExcuse();
            }
            var valBytes = u_valBytes.value();
            if (valBytes == null) {
                return TGS_UnionExcuse.of("");
            }
            return TGS_UnionExcuse.of(TS_StringUtils.toString(valBytes));
        }

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, int colIdx) {
            var u_valBytes = BlobBytes.get(resultSet, colIdx);
            if (u_valBytes.isExcuse()) {
                return u_valBytes.toExcuse();
            }
            var valBytes = u_valBytes.value();
            if (valBytes == null) {
                return TGS_UnionExcuse.of("");
            }
            return TGS_UnionExcuse.of(TS_StringUtils.toString(valBytes));
        }
    }

    public static class Lng {

        public static TGS_UnionExcuse<Long> get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            var u_scroll = Row.scrll(resultSet, rowIndex);
            if (u_scroll.isExcuse()) {
                return u_scroll.toExcuse();
            }
            return get(resultSet, columnName.toString());
        }

        public static TGS_UnionExcuse<Long> get(ResultSet resultSet, int rowIndex, int colIndex) {
            var u_scroll = Row.scrll(resultSet, rowIndex);
            if (u_scroll.isExcuse()) {
                return u_scroll.toExcuse();
            }
            return get(resultSet, colIndex);
        }

        public static TGS_UnionExcuse<Long> get(ResultSet resultSet, int colIndex) {
            try {
                return TGS_UnionExcuse.of(resultSet.getLong(colIndex + 1));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<Long> get(ResultSet resultSet, CharSequence columnName) {
            try {
                return TGS_UnionExcuse.of(resultSet.getLong(columnName.toString()));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }
    }

    public static class LngArr {

        public static TGS_UnionExcuse<List<Long>> get(ResultSet rs, int colIndex) {
            List<Long> target = TGS_ListUtils.of();
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            for (var ri = 0; ri < u_rowSize.value(); ri++) {
                var u_lngGet = Lng.get(rs, ri, colIndex);
                if (u_lngGet.isExcuse()) {
                    return u_lngGet.toExcuse();
                }
                target.add(u_lngGet.value());
            }
            return TGS_UnionExcuse.of(target);
        }

        public static TGS_UnionExcuse<List<Long>> get(ResultSet rs, CharSequence columnName) {
            List<Long> target = TGS_ListUtils.of();
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            for (var ri = 0; ri < u_rowSize.value(); ri++) {
                var u_lngGet = Lng.get(rs, ri, columnName.toString());
                if (u_lngGet.isExcuse()) {
                    return u_lngGet.toExcuse();
                }
                target.add(u_lngGet.value());
            }
            return TGS_UnionExcuse.of(target);
        }
    }

    public static class Str {

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, int ri, int ci) {
            var u_scroll = Row.scrll(resultSet, ri);
            if (u_scroll.isExcuse()) {
                return u_scroll.toExcuse();
            }
            return get(resultSet, ci);
        }

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, int rowIndex, CharSequence columnName) {
            var u_scroll = Row.scrll(resultSet, rowIndex);
            if (u_scroll.isExcuse()) {
                return u_scroll.toExcuse();
            }
            return get(resultSet, columnName);
        }

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, int ci) {
            try {
                return TGS_UnionExcuse.of(resultSet.getString(ci + 1));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }

        public static TGS_UnionExcuse<String> get(ResultSet resultSet, CharSequence columnName) {
            try {
                return TGS_UnionExcuse.of(resultSet.getString(columnName.toString()));
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }
    }

    public static class StrArr {

        public static TGS_UnionExcuse<List<String>> get(ResultSet rs, int ci) {
            List<String> target = TGS_ListUtils.of();
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            for (var ri = 0; ri < u_rowSize.value(); ri++) {
                var u_strGet = Str.get(rs, ri, ci);
                if (u_strGet.isExcuse()) {
                    return u_strGet.toExcuse();
                }
                var vi = u_strGet.value();
                d.ci("StrArr.get", "ri", ri, "ci", ci, "vi", vi);
                target.add(vi);
            }
            return TGS_UnionExcuse.of(target);
        }

        public static TGS_UnionExcuse<List<String>> get(ResultSet rs, CharSequence columnName) {
            List<String> target = TGS_ListUtils.of();
            var u_rowSize = Row.size(rs);
            if (u_rowSize.isExcuse()) {
                return u_rowSize.toExcuse();
            }
            for (var ri = 0; ri < u_rowSize.value(); ri++) {
                var u_strGet = Str.get(rs, ri, columnName.toString());
                if (u_strGet.isExcuse()) {
                    return u_strGet.toExcuse();
                }
                var vi = u_strGet.value();
                d.ci("StrArr.get", "ri", ri, "cm", columnName, "vi", vi);
                target.add(vi);
            }
            return TGS_UnionExcuse.of(target);
        }
    }

    public static class Date {

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, int rowIndex, CharSequence colName) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, colName);
        }

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, int rowIndex, int colIdx) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, colIdx);
        }

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, CharSequence colName) {
            var u_val = Lng.get(resultSet, colName);
            if (u_val.isExcuse()) {
                return u_val.toExcuse();
            }
            return TGS_UnionExcuse.of(TGS_Time.ofDate(u_val.value()));
        }

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, int colIdx) {
            var u_val = Lng.get(resultSet, colIdx);
            if (u_val.isExcuse()) {
                return u_val.toExcuse();
            }
            return TGS_UnionExcuse.of(TGS_Time.ofDate(u_val.value()));
        }
    }

    public static class Time {

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, int rowIndex, CharSequence colName) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, colName);
        }

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, int rowIndex, int colIdx) {
            var u = Row.scrll(resultSet, rowIndex);
            if (u.isExcuse()) {
                return u.toExcuse();
            }
            return get(resultSet, colIdx);
        }

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, CharSequence colName) {
            var u_val = Lng.get(resultSet, colName);
            if (u_val.isExcuse()) {
                return u_val.toExcuse();
            }
            return TGS_UnionExcuse.of(TGS_Time.ofTime(u_val.value()));
        }

        public static TGS_UnionExcuse<TGS_Time> get(ResultSet resultSet, int colIdx) {
            var u_val = Lng.get(resultSet, colIdx);
            if (u_val.isExcuse()) {
                return u_val.toExcuse();
            }
            return TGS_UnionExcuse.of(TGS_Time.ofTime(u_val.value()));
        }
    }
}
