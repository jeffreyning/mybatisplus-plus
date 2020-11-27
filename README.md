# mybatisplus-plus
mybatisplus-plus对mybatisplus的一些功能补充

**自动填充优化功能**
原生mybatisplus只能做%s+1和now两种填充，mybatisplus-plus在插入或更新时对指定字段进行自定义复杂sql填充。<br>
需要在实体类字段上用原生注解@TableField设置fill=FieldFill.INSERT fill=FieldFill.UPDATE或fill=FieldFill.INSERT_UPDATE否则不会触发自定义填充<br>
mybatisplus-plus使用@InsertFill注解触发插入时，执行注解中自定义的sql填充实体类字段<br>
mybatisplus-plus使用@UpdateFill注解触发更新时，执行注解中自定义的sql填充实体类字段<br>

**从中央库引入jar**
````
    <dependency>
        <groupId>com.github.jeffreyning</groupId>
        <artifactId>mybatisplus-plus</artifactId>
        <version>0.0.1-RELEASE</version>
    </dependency>
````

**在实体类字段上设置@InsertFill，在插入时对seqno字段自动填充复杂计算值**
查询当前最大的seqno值并加3，转换成10位字符串，不够位数时用0填充
````
    @TableField(value="seqno",fill=FieldFill.INSERT )
    @InsertFill("select lpad(max(seqno)+3,10,'0') from test")
    private String seqno;
````

**在实体类字段上设置@InsertFill @UpdateFill，插入和更新时使用当前时间填充**
````
    @InsertFill("select now()")
    @UpdateFill("select now()")
    @TableField(value="update_time",fill=FieldFill.INSERT_UPDATE)
    private Date updateTime;
````

**在启动类中使用@EnableMPP启动扩展自定义填充功能**
````
@SpringBootApplication
@EnableMPP
public class PlusDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlusDemoApplication.class, args);
    }
}
````

**demo下载**
mybatisplus-plus示例工程下载地址
链接：https://pan.baidu.com/s/1EkDUW5zbtIjsvFJFf2IWVg 

扫描订阅公众号，回复"plus"获取下载密码
![Image text](http://www.jrnsoft.com/qrcode_for_gh.jpg)

