package ru.synergy.mvpsampleproject.mvp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.synergy.mvpsampleproject.User;
import ru.synergy.mvpsampleproject.UserTable;
import ru.synergy.mvpsampleproject.database.DbHelper;

public class UsersModel {

    private final DbHelper dbHelper;

    public UsersModel(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void loadUsers(LoadUserCallback callback) {
        LoadUsersTask loadUsersTask = new LoadUsersTask(callback);
        loadUsersTask.execute();
    }

    public void addUser(ContentValues contentValues, CompleteCallback callback) {
        AddUserTask addUserTask = new AddUserTask(callback);
        addUserTask.execute(contentValues);
    }

    public void clearUsers(CompleteCallback completeCallback) {
        ClearUsersTask clearUsersTask = new ClearUsersTask(completeCallback); // передаем получаемый коллбэк
        clearUsersTask.execute();
    }


    interface LoadUserCallback {
        void onLoad(List<User> users);
    }

    interface CompleteCallback {
        void onComplete();
    }

    class LoadUsersTask extends AsyncTask<Void, Void, List<User>> {

        private final LoadUserCallback callback;

        LoadUsersTask(LoadUserCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<User> doInBackground(Void... params) {
            List<User> users = new LinkedList<>();
            //получаем курсор из базы данных
            Cursor cursor = dbHelper.getReadableDatabase().query(UserTable.TABLE, null, null, null, null, null, null);
            while (cursor.moveToNext()) { // пока курсор мож двигаться вперед, т.е. есть следующий
                User user = new User(); // идем по юзерам и достаем данные
                user.setId(cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN.ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN.NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN.EMAIL)));
                users.add(user);
            }
            cursor.close();
            return users;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            if (callback != null) {
                callback.onLoad(users);// мы должны вызвать коллбэк
            }
        }
    }

    class AddUserTask extends AsyncTask<ContentValues, Void, Void> {

        private final CompleteCallback callback;

        AddUserTask(CompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ContentValues... params) {
            ContentValues cvUser = params[0];
            dbHelper.getWritableDatabase().insert(UserTable.TABLE, null, cvUser);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onComplete(); // реагируем на коллбэк
            }
        }
    }

    class ClearUsersTask extends AsyncTask<Void, Void, Void> {

        private final CompleteCallback callback;

        ClearUsersTask(CompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dbHelper.getWritableDatabase().delete(UserTable.TABLE, null, null);
            try {
                TimeUnit.SECONDS.sleep(1); //задержка чтобы не пролетало все в момент
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }


}
