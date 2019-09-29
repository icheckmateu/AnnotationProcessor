package AnnotationProcessor;

import Annotation.Entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TableInfo {
    private String tableName;
    private Class<?>clazz;
    private  boolean needPersist=false;
    private Map<String,ColumnInfo>columns=new HashMap<String, ColumnInfo>();
    public TableInfo parse(Class<?> clazz)
        {
            this.clazz=clazz;
            this.tableName=clazz.getSimpleName();
            Annotation[]annotations=this.clazz.getAnnotations();
            for(Annotation annotation:annotations){
                if(annotation.annotationType().equals(Entity.class)){
                    this.needPersist=true;
                    Entity entity=(Entity)annotation;
                    if(!entity.value().equals("")){
                        this.tableName=entity.value();
                    }
                    break;
                }
            }
            if(this.needPersist){
                Field[] files=this.clazz.getDeclaredFields();
                for(Field field:files){
                    ColumnInfo column=new ColumnInfo();
                    column=column.parse(field);
                    if(column!=null){
                        this.columns.put(field.getName(),column);
                    }
                }
                return this;
            }else {
                return null;
            }

        }

    @Override
    public String toString() {
        StringBuilder sql=new StringBuilder();
        sql.append(Symbol.LINE);
        sql.append("CREATE TABLE ");
        sql.append(this.tableName+Symbol.BLANK);
        sql.append("(");
        for (ColumnInfo column:this.columns.values()){
            sql.append(Symbol.LINE);
            sql.append(Symbol.TAB);
            sql.append(column.toString());
        }
        sql.append(Symbol.LINE);
        sql.append(");");
        return sql.toString();
    }
    public class Symbol{
        public  static  final String BLANK=" ";
        public  static  final String TAB="\t";
        public  static  final String LINE="\n";
    }
}
