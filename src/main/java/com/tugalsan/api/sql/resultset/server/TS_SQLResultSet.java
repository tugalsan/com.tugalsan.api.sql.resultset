package com.tugalsan.api.sql.resultset.server;

import com.tugalsan.api.function.client.TGS_Func_In1;
import com.tugalsan.api.function.client.TGS_Func_In2;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.unsafe.client.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

public class TS_SQLResultSet {

    final private static TS_Log d = TS_Log.of(TS_SQLResultSet.class);

    public static TS_SQLResultSet of(ResultSet resultSet) {
        return new TS_SQLResultSet(resultSet);
    }

    public TS_SQLResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
        this.meta = new Meta(this);
        this.html = new Html(this);
        this.bytes = new BlobBytes(this);
        this.bytesStr = new BlobStr(this);
        this.col = new Col(this);
        this.row = new Row(this);
        this.table = new Table(this);
        this.lng = new Lng(this);
        this.lngArr = new LngArr(this);
        this.str = new Str(this);
        this.strArr = new StrArr(this);
        this.date = new Date(this);
        this.time = new Time(this);
        this.obj = new Obj(this);
    }
    final public ResultSet resultSet;
    final public Meta meta;
    final public Html html;
    final public BlobBytes bytes;
    final public BlobStr bytesStr;
    final public Col col;
    final public Row row;
    final public Table table;
    final public Lng lng;
    final public LngArr lngArr;
    final public Str str;
    final public StrArr strArr;
    final public Date date;
    final public Time time;
    final public Obj obj;

    public void walkCols(TGS_Func_In1<TS_SQLResultSet> onEmpty, TGS_Func_In1<Integer> ci) {
        if (col.isEmpty()) {
            if (onEmpty != null) {
                onEmpty.run(this);
            }
            return;
        }
        IntStream.range(0, col.size()).forEachOrdered(idx_ci -> {
            ci.run(idx_ci);
        });
    }

    public void walkRows(TGS_Func_In1<TS_SQLResultSet> onEmpty, TGS_Func_In1<Integer> ri) {
        if (row.isEmpty()) {
            if (onEmpty != null) {
                onEmpty.run(this);
            }
            return;
        }
        IntStream.range(0, row.size()).forEachOrdered(idx_ri -> {
            row.scrll(idx_ri);
            ri.run(idx_ri);
        });
    }

    public void walkCells(TGS_Func_In1<TS_SQLResultSet> onEmpty, TGS_Func_In2<Integer, Integer> ri_ci) {
        if (row.isEmpty()) {
            if (onEmpty != null) {
                onEmpty.run(this);
            }
            return;
        }
        IntStream.range(0, row.size()).forEachOrdered(ri -> {
            row.scrll(ri);
            IntStream.range(0, col.size()).forEachOrdered(ci -> {
                ri_ci.run(ri, ci);
            });
        });
    }

    public static class Meta {

        public Meta(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public ResultSetMetaData get() {
            return TS_SQLResultSetUtils.Meta.get(resultSet.resultSet);
        }

        public String command() {
            return TGS_UnSafe.call(() -> resultSet.resultSet.getStatement().toString(), e -> TGS_StringUtils.cmn().concat("Error on ", d.className, " ", e.getMessage()));
        }
    }

    public class Html {

        public Html(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public String table(int fontsizeHeader, int fontsizeData) {
            return TS_SQLResultSetUtils.Html.table(resultSet.resultSet, fontsizeHeader, fontsizeData);
        }
    }

    public static class Obj {

        public Obj(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public Object get(int rowIndex, CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, rowIndex, columnName);
        }

        public Object get(int rowIndex, int colIndex) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public Object get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, columnName);
        }

        public Object get(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, colIdx);
        }
    }

    public static class BlobBytes {

        public BlobBytes(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public byte[] get(int rowIndex, CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, rowIndex, columnName);
        }

        public byte[] get(int rowIndex, int colIndex) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public byte[] get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, columnName);
        }

        public byte[] get(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, colIdx);
        }
    }

    public static class BlobStr {

        public BlobStr(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public String get(int rowIndex, CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, rowIndex, columnName);
        }

        public String get(int rowIndex, int colIndex) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public String get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, columnName);
        }

        public String get(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, colIdx);
        }
    }

    public static class Col {

        public Col(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public boolean isEmpty() {
            return TS_SQLResultSetUtils.Col.isEmpty(resultSet.resultSet);
        }

        public int size() {
            if (size != null) {
                return size;
            }
            return size = TS_SQLResultSetUtils.Col.size(resultSet.resultSet);
        }
        private Integer size = null;

        public String name(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Col.name(resultSet.resultSet, colIdx);
        }

        public String label(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Col.label(resultSet.resultSet, colIdx);
        }
    }

    public static class Row {

        public Row(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public boolean scrll(int rowIndex) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size())) {
                return false;
            }
            TS_SQLResultSetUtils.Row.scrll(resultSet.resultSet, rowIndex);
            return true;
        }

        public void scrllBottom() {
            TS_SQLResultSetUtils.Row.scrllBottom(resultSet.resultSet);
        }

        public void scrllTop() {
            TS_SQLResultSetUtils.Row.scrllTop(resultSet.resultSet);
        }

        public int curIdx() {
            return TS_SQLResultSetUtils.Row.curIdx(resultSet.resultSet);
        }

        public boolean isEmpty() {
            return TS_SQLResultSetUtils.Row.isEmpty(resultSet.resultSet);
        }

        public int size() {
            if (size != null) {
                return size;
            }
            return size = TS_SQLResultSetUtils.Row.size(resultSet.resultSet);
        }
        private Integer size = null;

        public List<TGS_SQLCellAbstract> get(int rowIndex) {
            return get(rowIndex, false);
        }

        public List<TGS_SQLCellAbstract> get(int rowIndex, boolean skipTypeByte) {
            d.ci("Row.get", "#0", rowIndex);
            var rs = TS_SQLResultSet.of(resultSet.resultSet);
            d.ci("Row.get", "#1");
            if (!rs.row.scrll(rowIndex)) {
                d.ci("Row.get", "#2");
                return null;
            }
            d.ci("Row.get", "#3");
            List<TGS_SQLCellAbstract> row = TGS_ListUtils.of();
            d.ci("Row.get", "#4");
            IntStream.range(0, rs.col.size()).forEachOrdered(ci -> {
                d.ci("Row.get", "#5");
                var ct = TGS_SQLColTyped.of(rs.col.name(ci));
                if (ct.familyLng()) {
                    row.add(new TGS_SQLCellLNG(rs.lng.get(ci)));
                    return;
                }
                if (ct.familyStr()) {
                    row.add(new TGS_SQLCellSTR(rs.str.get(ci)));
                    return;
                }
                if (ct.typeBytesStr()) {
                    row.add(new TGS_SQLCellBYTESSTR(rs.bytesStr.get(ci)));
                    return;
                }
                if (ct.familyBytes()) {
                    row.add(skipTypeByte ? new TGS_SQLCellBYTES() : new TGS_SQLCellBYTES(rs.bytes.get(ci)));
                    return;
                }
            });
            d.ci("Row.get", "#6");
            return row;
        }
    }

    public static class Table {

        public Table(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public List<List<TGS_SQLCellAbstract>> get(TS_ThreadSyncTrigger servletKillTrigger) {
            return get(servletKillTrigger, false);
        }

        public List<List<TGS_SQLCellAbstract>> get(TS_ThreadSyncTrigger servletKillTrigger, boolean skipTypeBytes) {
            List<List<TGS_SQLCellAbstract>> table = TGS_ListUtils.of();
            if (resultSet.row.isEmpty()) {
                return table;
            }
            var size = resultSet.row.size();
            for (var ri = 0; ri < size; ri++) {
                if (servletKillTrigger.hasTriggered()) {
                    return table;
                }
                table.add(resultSet.row.get(ri, skipTypeBytes));
            }
            return table;
        }

    }

    public static class Lng {

        public Lng(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public Long get(int rowIndex, CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Lng.get(resultSet.resultSet, rowIndex, columnName);
        }

        public Long get(int rowIndex, int colIndex) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Lng.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public Long get(int colIndex) {
            if (!TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Lng.get(resultSet.resultSet, colIndex);
        }

        public Long get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Lng.get(resultSet.resultSet, columnName);
        }
    }

    public static class LngArr {

        public LngArr(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public List<Long> get(int colIndex) {
            if (!TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.LngArr.get(resultSet.resultSet, colIndex);
        }

        public List<Long> get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.LngArr.get(resultSet.resultSet, columnName);
        }
    }

    public static class Str {

        public Str(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public String get(int rowIndex, CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Str.get(resultSet.resultSet, rowIndex, columnName);
        }

        public String get(int rowIndex, int colIndex) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Str.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public String get(int colIndex) {
            if (!TS_SQLResultSetUtils.Col.valid(colIndex, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Str.get(resultSet.resultSet, colIndex);
        }

        public String get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Str.get(resultSet.resultSet, columnName);
        }
    }

    public static class StrArr {

        public StrArr(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public List<String> get(int ci) {
            if (!TS_SQLResultSetUtils.Col.valid(ci, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.StrArr.get(resultSet.resultSet, ci);
        }

        public List<String> get(CharSequence columnName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName)) {
                return null;
            }
            return TS_SQLResultSetUtils.StrArr.get(resultSet.resultSet, columnName);
        }
    }

    public static class Date {

        public Date(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_Time get(int rowIndex, CharSequence colName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, colName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Date.get(resultSet.resultSet, rowIndex, colName);
        }

        public TGS_Time get(int rowIndex, int colIdx) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Date.get(resultSet.resultSet, rowIndex, colIdx);
        }

        public TGS_Time get(CharSequence colName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, colName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Date.get(resultSet.resultSet, colName);
        }

        public TGS_Time get(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Date.get(resultSet.resultSet, colIdx);
        }
    }

    public static class Time {

        public Time(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;
        }
        final private TS_SQLResultSet resultSet;

        public TGS_Time get(int rowIndex, CharSequence colName) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, colName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Time.get(resultSet.resultSet, rowIndex, colName);
        }

        public TGS_Time get(int rowIndex, int colIdx) {
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, resultSet.row.size()) || !TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Time.get(resultSet.resultSet, rowIndex, colIdx);
        }

        public TGS_Time get(CharSequence colName) {
            if (!TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, colName)) {
                return null;
            }
            return TS_SQLResultSetUtils.Time.get(resultSet.resultSet, colName);
        }

        public TGS_Time get(int colIdx) {
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, resultSet.col.size())) {
                return null;
            }
            return TS_SQLResultSetUtils.Time.get(resultSet.resultSet, colIdx);
        }
    }
}
