package ru.synergy.mvpsampleproject.mvp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.synergy.mvpsampleproject.R;
import ru.synergy.mvpsampleproject.User;
import ru.synergy.mvpsampleproject.UserAdapter;
import ru.synergy.mvpsampleproject.database.DbHelper;
// отображение данных, представлений
public class UsersActivity extends AppCompatActivity {

    private UserAdapter userAdapter;

    private EditText editTextName;
    private EditText editTextEmail;
    private ProgressDialog progressDialog;

    private UsersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        init();//протыкиваем все визуальные поля, собираем их в нужные места
    }

    private void init() {

        editTextName = (EditText) findViewById(R.id.name);
        editTextEmail = (EditText) findViewById(R.id.email);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.add();
            } // вызов метода для презентера
        });
//вся логика содержится в презентере
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clear();
            } // вызов метода для презентера
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        userAdapter = new UserAdapter(); // выставляем адаптер

        RecyclerView userList = (RecyclerView) findViewById(R.id.list);
        userList.setLayoutManager(layoutManager);
        userList.setAdapter(userAdapter);

//формат инициализации объектов
        DbHelper dbHelper = new DbHelper(this); //создаем ДБ хэлпер
        UsersModel usersModel = new UsersModel(dbHelper);
        presenter = new UsersPresenter(usersModel);
        presenter.attachView(this);
        presenter.viewIsReady();
    }

    public UserData getUserData() { // метод который возвращает юзер дата
        UserData userData = new UserData();
        userData.setName(editTextName.getText().toString());
        userData.setEmail(editTextEmail.getText().toString());
        //получаем данные с экрана
        return userData;
    }
//далее идет только визуальная часть
    public void showUsers(List<User> users) {
        userAdapter.setData(users);
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "", getString(R.string.please_wait));
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() { // метод жизненного цикла - обязательно
        super.onDestroy();
        presenter.detachView();
    }
}

