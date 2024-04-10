package com.tugalsan.api.sql.resultset.server;

import com.tugalsan.api.runnable.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
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

    public TGS_UnionExcuseVoid walkCols(TGS_RunnableType1<TS_SQLResultSet> onEmpty, TGS_RunnableType1<Integer> ci) {
        var u_colIsEmpty = col.isEmpty();
        if (u_colIsEmpty.isExcuse()) {
            return u_colIsEmpty.toExcuseVoid();
        }
        if (u_colIsEmpty.value()) {
            if (onEmpty != null) {
                onEmpty.run(this);
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }
        var u_colSize = col.size();
        if (u_colSize.isExcuse()) {
            return u_colSize.toExcuseVoid();
        }
        IntStream.range(0, u_colSize.value()).forEachOrdered(idx_ci -> {
            ci.run(idx_ci);
        });
        return TGS_UnionExcuseVoid.ofVoid();
    }

    public TGS_UnionExcuseVoid walkRows(TGS_RunnableType1<TS_SQLResultSet> onEmpty, TGS_RunnableType1<Integer> ri) {
        var u_rowIsEmpty = row.isEmpty();
        if (u_rowIsEmpty.isExcuse()) {
            return u_rowIsEmpty.toExcuseVoid();
        }
        if (u_rowIsEmpty.value()) {
            if (onEmpty != null) {
                onEmpty.run(this);
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }
        var u_rowSize = row.size();
        if (u_rowSize.isExcuse()) {
            return u_rowSize.toExcuseVoid();
        }
        for (var idx_ri = 0; idx_ri < u_rowSize.value(); idx_ri++) {
            var u_scroll = row.scrll(idx_ri);
            if (u_scroll.isExcuse()) {
                return u_scroll;
            }
            ri.run(idx_ri);
        }
        return TGS_UnionExcuseVoid.ofVoid();
    }

    public TGS_UnionExcuseVoid walkCells(TGS_RunnableType1<TS_SQLResultSet> onEmpty, TGS_RunnableType2<Integer, Integer> ri_ci) {
        var u_rowIsEmpty = row.isEmpty();
        if (u_rowIsEmpty.isExcuse()) {
            return u_rowIsEmpty.toExcuseVoid();
        }
        if (u_rowIsEmpty.value()) {
            if (onEmpty != null) {
                onEmpty.run(this);
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }
        var u_rowSize = row.size();
        if (u_rowSize.isExcuse()) {
            return u_rowSize.toExcuseVoid();
        }
        for (var ri = 0; ri < u_rowSize.value(); ri++) {
            var u_scroll = row.scrll(ri);
            if (u_scroll.isExcuse()) {
                return u_scroll;
            }
            var u_colSize = col.size();
            if (u_colSize.isExcuse()) {
                return u_colSize.toExcuseVoid();
            }
            for (var ci = 0; ci < u_colSize.value(); ci++) {
                ri_ci.run(ri, ci);
            }
        }
        return TGS_UnionExcuseVoid.ofVoid();
    }

    public static class Meta {

        public Meta(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuse<ResultSetMetaData> get() {
            return TS_SQLResultSetUtils.Meta.get(resultSet.resultSet);
        }

        public TGS_UnionExcuse<String> command() {
            try {
                return TGS_UnionExcuse.of(resultSet.resultSet.getStatement().toString());
            } catch (SQLException ex) {
                return TGS_UnionExcuse.ofExcuse(ex);
            }
        }
    }

    public class Html {

        public Html(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuse<String> table(int fontsizeHeader, int fontsizeData) {
            return TS_SQLResultSetUtils.Html.table(resultSet.resultSet, fontsizeHeader, fontsizeData);
        }
    }

    public static class Obj {

        public Obj(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuse<Object> get(int rowIndex, CharSequence columnName) {
            var u_resultSet_row_size = resultSet.row.size();
            if (u_resultSet_row_size.isExcuse()) {
                return u_resultSet_row_size.toExcuse();
            }
            var rowValid = TS_SQLResultSetUtils.Row.valid(rowIndex, u_resultSet_row_size.value());
            if (!rowValid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "Obj.get", "row is not valid");
            }
            var u_colValid = TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName);
            if (u_colValid.isExcuse()) {
                return u_colValid.toExcuse();
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, rowIndex, columnName);
        }

        public TGS_UnionExcuse<Object> get(int rowIndex, int colIndex) {
            var u_resultSet_row_size = resultSet.row.size();
            if (u_resultSet_row_size.isExcuse()) {
                return u_resultSet_row_size.toExcuse();
            }
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, u_resultSet_row_size.value())) {
                return TGS_UnionExcuse.ofExcuse(d.className, "Obj.get", "row is not valid");
            }
            var u_resultSet_col_size = resultSet.col.size();
            if (u_resultSet_col_size.isExcuse()) {
                return u_resultSet_col_size.toExcuse();
            }
            if (!TS_SQLResultSetUtils.Col.valid(colIndex, u_resultSet_col_size.value())) {
                return TGS_UnionExcuse.ofExcuse(d.className, "Obj.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public TGS_UnionExcuse<Object> get(CharSequence columnName) {
            var u_colValid = TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName);
            if (u_colValid.isExcuse()) {
                return u_colValid.toExcuse();
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, columnName);
        }

        public TGS_UnionExcuse<Object> get(int colIdx) {
            var u_resultSet_col_size = resultSet.col.size();
            if (u_resultSet_col_size.isExcuse()) {
                return u_resultSet_col_size.toExcuse();
            }
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, u_resultSet_col_size.value())) {
                return TGS_UnionExcuse.ofExcuse(d.className, "Obj.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.Obj.get(resultSet.resultSet, colIdx);
        }
    }

    public static class BlobBytes {

        public BlobBytes(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuse<byte[]> get(int rowIndex, CharSequence columnName) {
            var u_resultSet_row_size = resultSet.row.size();
            if (u_resultSet_row_size.isExcuse()) {
                return u_resultSet_row_size.toExcuse();
            }
            var rowValid = TS_SQLResultSetUtils.Row.valid(rowIndex, u_resultSet_row_size.value());
            if (!rowValid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobBytes.get", "row is not valid");
            }
            var u_colValid = TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName);
            if (u_colValid.isExcuse()) {
                return u_colValid.toExcuse();
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, rowIndex, columnName);
        }

        public TGS_UnionExcuse<byte[]> get(int rowIndex, int colIndex) {
            var u_resultSet_row_size = resultSet.row.size();
            if (u_resultSet_row_size.isExcuse()) {
                return u_resultSet_row_size.toExcuse();
            }
            if (!TS_SQLResultSetUtils.Row.valid(rowIndex, u_resultSet_row_size.value())) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobBytes.get", "row is not valid");
            }
            var u_resultSet_col_size = resultSet.col.size();
            if (u_resultSet_col_size.isExcuse()) {
                return u_resultSet_col_size.toExcuse();
            }
            if (!TS_SQLResultSetUtils.Col.valid(colIndex, u_resultSet_col_size.value())) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobBytes.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public TGS_UnionExcuse<byte[]> get(CharSequence columnName) {
            var u_colValid = TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName);
            if (u_colValid.isExcuse()) {
                return u_colValid.toExcuse();
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, columnName);
        }

        public TGS_UnionExcuse<byte[]> get(int colIdx) {
            var u_resultSet_col_size = resultSet.col.size();
            if (u_resultSet_col_size.isExcuse()) {
                return u_resultSet_col_size.toExcuse();
            }
            if (!TS_SQLResultSetUtils.Col.valid(colIdx, u_resultSet_col_size.value())) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobBytes.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.BlobBytes.get(resultSet.resultSet, colIdx);
        }
    }

    public static class BlobStr {

        public BlobStr(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuse<String> get(int rowIndex, CharSequence columnName) {
            var u_row_size = resultSet.row.size();
            if (u_row_size.isExcuse()) {
                return u_row_size.toExcuse();
            }
            var rowValid = TS_SQLResultSetUtils.Row.valid(rowIndex, u_row_size.value());
            if (!rowValid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "row is not valid");
            }
            var u_colValid = TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName);
            if (u_colValid.isExcuse()) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, rowIndex, columnName);
        }

        public TGS_UnionExcuse<String> get(int rowIndex, int colIndex) {
            var u_row_size = resultSet.row.size();
            if (u_row_size.isExcuse()) {
                return u_row_size.toExcuse();
            }
            var u_row_valid = TS_SQLResultSetUtils.Row.valid(rowIndex, u_row_size.value());
            if (!u_row_valid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "row is not valid");
            }
            var u_col_size = resultSet.col.size();
            if (u_col_size.isExcuse()) {
                return u_col_size.toExcuse();
            }
            var col_valid = TS_SQLResultSetUtils.Col.valid(colIndex, u_col_size.value());
            if (!col_valid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, rowIndex, colIndex);
        }

        public TGS_UnionExcuse<String> get(CharSequence columnName) {
            var u_col_valid = TS_SQLResultSetUtils.Col.valid(resultSet.resultSet, columnName);
            if (u_col_valid.isExcuse()) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, columnName);
        }

        public TGS_UnionExcuse<String> get(int colIdx) {
            var u_col_size = resultSet.col.size();
            if (u_col_size.isExcuse()) {
                return u_col_size.toExcuse();
            }
            var u_col_valid = TS_SQLResultSetUtils.Col.valid(colIdx, u_col_size.value());
            if (!u_col_valid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.BlobStr.get(resultSet.resultSet, colIdx);
        }
    }

    public static class Col {

        public Col(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuse<Boolean> isEmpty() {
            return TS_SQLResultSetUtils.Col.isEmpty(resultSet.resultSet);
        }

        public TGS_UnionExcuse<Integer> size() {
            if (size != null) {
                return size;
            }
            return size = TS_SQLResultSetUtils.Col.size(resultSet.resultSet);
        }
        private TGS_UnionExcuse<Integer> size = null;

        public TGS_UnionExcuse<String> name(int colIdx) {
            var u_col_size = resultSet.col.size();
            if (u_col_size.isExcuse()) {
                return u_col_size.toExcuse();
            }
            var u_col_valid = TS_SQLResultSetUtils.Col.valid(colIdx, u_col_size.value());
            if (!u_col_valid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.Col.name(resultSet.resultSet, colIdx);
        }

        public TGS_UnionExcuse<String> label(int colIdx) {
            var u_col_size = resultSet.col.size();
            if (u_col_size.isExcuse()) {
                return u_col_size.toExcuse();
            }
            var u_col_valid = TS_SQLResultSetUtils.Col.valid(colIdx, u_col_size.value());
            if (!u_col_valid) {
                return TGS_UnionExcuse.ofExcuse(d.className, "BlobStr.get", "col is not valid");
            }
            return TS_SQLResultSetUtils.Col.label(resultSet.resultSet, colIdx);
        }
    }

    public static class Row {

        public Row(TS_SQLResultSet resultSet) {
            this.resultSet = resultSet;

        }
        final private TS_SQLResultSet resultSet;

        public TGS_UnionExcuseVoid scrll(int rowIndex) {
            var u_row_size = resultSet.row.size();
            if (u_row_size.isExcuse()) {
                return u_row_size.toExcuseVoid();
            }
            var u_row_valid = TS_SQLResultSetUtils.Row.valid(rowIndex, u_row_size.value());
            if (!u_row_valid) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "BlobStr.get", "row is not valid");
            }
            return TS_SQLResultSetUtils.Row.scrll(resultSet.resultSet, rowIndex);
        }

        public TGS_UnionExcuseVoid scrllBottom() {
            return TS_SQLResultSetUtils.Row.scrllBottom(resultSet.resultSet);
        }

        public TGS_UnionExcuseVoid scrllTop() {
            return TS_SQLResultSetUtils.Row.scrllTop(resultSet.resultSet);
        }

        public TGS_UnionExcuse<Integer> curIdx() {
            return TS_SQLResultSetUtils.Row.curIdx(resultSet.resultSet);
        }

        public TGS_UnionExcuse<Boolean> isEmpty() {
            return TS_SQLResultSetUtils.Row.isEmpty(resultSet.resultSet);
        }

        public TGS_UnionExcuse<Integer> size() {
            if (size != null) {
                return size;
            }
            return size = TS_SQLResultSetUtils.Row.size(resultSet.resultSet);
        }
        private TGS_UnionExcuse<Integer> size = null;

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

        public List<List<TGS_SQLCellAbstract>> get() {
            return get(false);
        }

        public List<List<TGS_SQLCellAbstract>> get(boolean skipTypeBytes) {
            List<List<TGS_SQLCellAbstract>> table = TGS_ListUtils.of();
            if (resultSet.row.isEmpty()) {
                return table;
            }
            var size = resultSet.row.size();
            IntStream.range(0, size).forEachOrdered(ri -> {
                table.add(resultSet.row.get(ri, skipTypeBytes));
            });
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
