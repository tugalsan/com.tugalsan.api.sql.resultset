module com.tugalsan.api.sql.resultset {
    requires java.sql;
    requires com.tugalsan.api.executable;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.string;
    requires com.tugalsan.api.compiler;
    requires com.tugalsan.api.time;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.sql.col.typed;
    requires com.tugalsan.api.sql.cell;
    exports com.tugalsan.api.sql.resultset.server;
}
