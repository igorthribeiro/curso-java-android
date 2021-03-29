package br.com.igordev.eminjectlib.dao;

import android.content.Context;
import android.content.pm.PackageManager;

import com.j256.ormlite.dao.Dao;

public class DaoHelper<T extends Entity> extends DatabaseHelper {

    private final Class<T> t;
    private Dao<T, Integer> dao;

    //The argument class "t" need have same type of generic type "<T>"
    protected DaoHelper(Context context, Class<T> t) throws PackageManager.NameNotFoundException {
        super(context);
        this.t = t;
    }

    public Dao<T, Integer> getDao() throws java.sql.SQLException {
        if (dao == null) {
            dao = getDao(t);
        }
        return dao;
    }
}
