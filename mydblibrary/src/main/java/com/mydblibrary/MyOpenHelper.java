package com.mydblibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.internal.UnsafeAllocator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Administrator on 2017/10/12.
 */
public class MyOpenHelper extends SQLiteOpenHelper implements IOpenHelper{


    private final static String ID = "id";


    public MyOpenHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    //增
    @Override
    public void save(Object obj) {
        //获取类类型
        Class<?> table = obj.getClass();
        //创建对应的表
        createTableIfNotExists(table);
        //具体实现保存数据方法
        save(obj, table, getWritableDatabase());
    }
    /**
     *  保存数据的主要操作
     * @param obj 数据库对象
     * @param table 对象类类型
     * @param db 操作数据库
     */
    private void save(Object obj, Class<?> table, SQLiteDatabase db) {
        //将一个对象中的所有字段添加到该数据集中
        ContentValues contentValues = new ContentValues();
        //通过反射获取一个类中的所有属性
        Field[] declaredFields = table.getDeclaredFields();
        //遍历所有的属性
        for (Field field : declaredFields) {
            //获取对应的修饰类型
            int modifiers = field.getModifiers();
            //如果不是静态的就插入到数据库
            if (!Modifier.isStatic(modifiers)) {
                //设置一下数据访问权限为最高级别，也就是public
                field.setAccessible(true);
                try {
                    //将每一个字段的信息保存到数据集中
                    contentValues.put(field.getName(), field.get(obj) + "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //对于一般的数据操作，我们采用通常是insert来插入数据，但是为了防止同一个对象的数据进行刷新，所以采用直接替换掉
        db.replace(table.getName().replaceAll("\\.", "_"), null, contentValues);
    }
    /*
    * ---------------------------------------------------------------------------------------------------------------------------------------------------------
    * */

    //删
    @Override
    public void delete(Class<?> table, int Id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String where = ID+ "=?";
        String[] whereValue = {Integer.toString(Id)};

        db.delete(table.getName().replaceAll("\\.", "_"), where, whereValue);




    }
    /*
   *---------------------------------------------------------------------------------------------------------------------------------------------------------
    * */
    //改
    @Override
    public void udadta(Class<?> table,Object obj, int Id) {
        SQLiteDatabase db =this.getWritableDatabase();
        //将一个对象中的所有字段添加到该数据集中
        ContentValues contentValues = new ContentValues();
        //通过反射获取一个类中的所有属性
        Field[] declaredFields = table.getDeclaredFields();
        //遍历所有的属性
        for (Field field : declaredFields) {
            //获取对应的修饰类型
            int modifiers = field.getModifiers();
            //如果不是静态的就插入到数据库
            if (!Modifier.isStatic(modifiers)) {
                //设置一下数据访问权限为最高级别，也就是public
                field.setAccessible(true);
                try {
                    //将每一个字段的信息保存到数据集中
                    contentValues.put(field.getName(), field.get(obj) + "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //对于一般的数据操作，我们采用通常是insert来插入数据，但是为了防止同一个对象的数据进行刷新，所以采用直接替换掉
        String where = ID+"=?";
        String[] whereValue = {Integer.toString(Id)};
        db.update(table.getName().replaceAll("\\.", "_"), contentValues, where, whereValue);

    }
    /*
    * ---------------------------------------------------------------------------------------------------------------------------------------------------------
    * */

    //查
    @Override
    public <T> List<T> queryAll(Class<T> table) {
        //如果该表不存在数据库中，则不需要进行操作
        if (!isTableExists(table)) {
            return null;
        }
        SQLiteDatabase db = getReadableDatabase();
        //获取表名，因为表名是采用完全包名的形式存储，按照表名规则，不允许有 "." 的存在,所以采用"_"进行替换
        String tableName = table.getName().replaceAll("\\.", "_");
        //通过表名查询所有的数据
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        //通过initList拿到对应的数据
        List<T> result = initList(table, cursor);
        //关闭游标
        cursor.close();
        //返回结果
        return result;

    }
   /*
   * ---------------------------------------------------------------------------------------------------------------------------------------------------------
   * */

    /**
     * 判断表格是否存在
     * @param table
     * @return
     */
    private boolean isTableExists(Class table) {
        SQLiteDatabase db = getReadableDatabase();
        //查询表是否存在
        Cursor cursor = db.query("sqlite_master", null, "type = 'table' and name = ?", new String[]{table.getName().replaceAll("\\.", "_")}, null, null, null);
        boolean isExists = cursor.getCount() > 0;
        cursor.close();
        return isExists;
    }

    /**
     * 如果表格不存在就创建该表。如果存在就不创建
     * @param table
     */
    private void createTableIfNotExists(Class table) {
        if (!isTableExists(table)) {
            SQLiteDatabase db = getWritableDatabase();
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS ");
            builder.append(table.getName().replaceAll("\\.", "_"));
            builder.append(" (");
            builder.append(ID+" Integer PRIMARY KEY AUTOINCREMENT,");
            for (Field field : table.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (!field.equals(id) && !Modifier.isStatic(modifiers)) {
                    builder.append(field.getName()).append(",");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            db.execSQL(builder.toString());
        }
    }

    /**
     * 这个方法的主要功能是将数据中查询到的数据放到集合中。
     * 类似于我们查询到对应的数据重新封装到一个对象中，然后把这个对象
     * 放入集合中。这样就能拿到我们的数据集了
     *
     *   List<T> result = initList(table, cursor);
     *
     * @param table
     * @param cursor
     * @param <T>
     * @return
     */
    private <T> List<T> initList(Class<T> table, Cursor cursor) {
        List<T> result = new ArrayList<>();
        //这里可能大家不了解，这是Gson为我们提供的一个通过JDK内部API 来创建对象实例，这里不做过多讲解
        UnsafeAllocator allocator = UnsafeAllocator.create();
        while (cursor.moveToNext()) {
            try {
                //创建具体的实例
                T t = allocator.newInstance(table);
                boolean flag = true;
                //遍历所有的游标数据
                for (int i = 0; i < cursor.getColumnCount(); i++) {

                    //每次都去查找该类中有没有自带的id，如果没有，就不应该执行下面的语句
                    //因为下面获取属性名时，有一个异常抛出，要是找不到属性就会结束这个for循环
                    //后面的所有数据就拿不到了。
                    if(flag){
                        Field fieldId = getFieldId(table);
                        if(fieldId == null){
                            flag = !flag;
                            continue;
                        }
                    }
                    //通过列名获取对象中对应的属性名
                    Field field = table.getDeclaredField(cursor.getColumnName(i));
                    //获取属性的类型
                    Class<?> type = field.getType();
                    //设置属性的访问权限为最高权限，因为要设置对应的数据
                    field.setAccessible(true);
                    //获取到数据库中的值，由于sqlite是采用若语法，都可以使用getString来获取
                    String value = cursor.getString(i);
                    //通过判断类型，保存到指定类型的属性中，这里判断了我们常用的数据类型。
                    if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
                        field.set(t, Byte.parseByte(value));
                    } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
                        field.set(t, Short.parseShort(value));
                    } else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
                        field.set(t, Integer.parseInt(value));
                    } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
                        field.set(t, Long.parseLong(value));
                    } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
                        field.set(t, Float.parseFloat(value));
                    } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
                        field.set(t, Double.parseDouble(value));
                    } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
                        field.set(t, value.charAt(0));
                    } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                        field.set(t, Boolean.parseBoolean(value));
                    } else if (type.equals(String.class)) {
                        field.set(t, value);
                    }
                }
                result.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 获取对象属性中的id字段，如果有就获取，没有就不获取
     * @param table
     * @return
     */
    private Field getFieldId(Class table) {
        Field fieldId = null;
        try {
            fieldId = table.getDeclaredField("_id");
            if (fieldId == null) {
                table.getDeclaredField("id");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return fieldId;
    }
}
