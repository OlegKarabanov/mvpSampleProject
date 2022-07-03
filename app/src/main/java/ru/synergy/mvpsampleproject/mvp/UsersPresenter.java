package ru.synergy.mvpsampleproject.mvp;

import android.content.ContentValues;
import android.text.TextUtils;

import java.util.List;

import ru.synergy.mvpsampleproject.R;
import ru.synergy.mvpsampleproject.User;
import ru.synergy.mvpsampleproject.UserTable;

public class UsersPresenter { //создаем класс презентер

    private UsersActivity view;
    private final UsersModel model;

    public UsersPresenter(UsersModel model) {
        this.model = model;
    } //конструктор который принимает модел

    public void attachView(UsersActivity usersActivity) {
        view = usersActivity;
    } // дает представление о работе
// будет вызван после создание
    public void detachView() {
        view = null;
    } // отвязывает представление от работы


    public void viewIsReady() {
        loadUsers();
    } //вызывается представлением, обертка для метода лоадюзерс

    public void loadUsers() {
        model.loadUsers(new UsersModel.LoadUserCallback() {
            @Override
            public void onLoad(List<User> users) {
                view.showUsers(users);
            } //переопределяем коллбэк
        });
    }

    public void add() { // при нажатии польз кнопки адд начинается логика
        UserData userData = view.getUserData(); // получаем юзерские данные из нашей активности
        if (TextUtils.isEmpty(userData.getName()) || TextUtils.isEmpty(userData.getEmail())) {// проверка на корректн
            view.showToast(R.string.empty_values); // если поля не пустые то идем дальше, если нет то ретенр
            return;
        }

        ContentValues cv = new ContentValues(2);
        cv.put(UserTable.COLUMN.NAME, userData.getName());
        cv.put(UserTable.COLUMN.EMAIL, userData.getEmail());
        view.showProgress(); // показывает сообщение
        model.addUser(cv, new UsersModel.CompleteCallback() {// создаем реализацию нового коллбэка
            @Override
            public void onComplete() {
                view.hideProgress();
                loadUsers();//загружаем юэеров и отбратно к стр.34
            }
        });
    }

    public void clear() {
        view.showProgress();
        model.clearUsers(new UsersModel.CompleteCallback() {
            @Override
            public void onComplete() {
                view.hideProgress();
                loadUsers();
            }
        });
    }

}

