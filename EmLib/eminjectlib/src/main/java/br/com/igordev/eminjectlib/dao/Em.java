package br.com.igordev.eminjectlib.dao;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class Em<T extends Entity> {

    private Class<T> clazz;
    private Context context;
    private DaoHelper<T> helper;
    private Dao<T, Integer> dao;


    protected Em(Context context, Class<T> clazz) {
        try {
            helper = new DaoHelper(context, clazz);
            dao = helper.getDao();
            this.clazz = clazz;
            this.context = context;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void post(T t) {
        try {
            if (t.getId() == null || t.getId() == 0) {
                dao.create(t);
            } else {
                dao.update(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public T findById(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Where<T, Integer> sqlWhere() {
        return dao.queryBuilder().where();
    }


    public void delete(T t) {
        try {
            dao.delete(t);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> list() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long count() {
        try {
            long count = dao.countOf();
            Log.i(getClass().getSimpleName(), "Quantidade de registros no cache: " + count);
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<T, Integer> getDao() {
        return dao;
    }

    public void deleteAllData() {
        try {
            TableUtils.clearTable(dao.getConnectionSource(), clazz);
        } catch (SQLException e) {
           throw new RuntimeException(e);
        }
    }

    public void close() {
        helper.close();
    }
}
