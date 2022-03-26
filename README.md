# mybatisplus-plus
mybatisplus-plus对mybatisplus的一些功能补充

**根据多个字段联合主键增删改查**
原生mybatisplus只支持一个主键，mpp支持多个字段联合主键（复合主键）增删改查，mapper需要继承MppBaseMapper<br>
实体类中联合主键的字段需要用@MppMultiId注解修饰<br>
如果需要在service使用多主键相关操作包括saveOrUpdateByMultiId和批量操作updateBatchByMultiId和saveOrUpdateBatchByMultiId，可以直接继承IMppService接口<br>

**优化分页插件实现在不分页时进行排序操作**
原生mybatisplus分页与排序是绑定的，mpp优化了分页插件，使用MppPaginationInterceptor插件<br>
在不分页的情况下支持排序操作<br>
page参数size设置为-1可实现不分页取全量数据，同时设置OrderItem可以实现排序<br>

**自动填充优化功能 & 自动扫描Entity类构建ResultMap功能**
原生mybatisplus只能做%s+1和now两种填充，mybatisplus-plus在插入或更新时对指定字段进行自定义复杂sql填充。<br>
需要在实体类字段上用原生注解@TableField设置fill=FieldFill.INSERT fill=FieldFill.UPDATE或fill=FieldFill.INSERT_UPDATE否则不会触发自定义填充<br>
mybatisplus-plus使用@InsertFill注解触发插入时，执行注解中自定义的sql填充实体类字段<br>
mybatisplus-plus使用@UpdateFill注解触发更新时，执行注解中自定义的sql填充实体类字段<br>
还可以自动填充主键字段,解决原生mybatisplus不支持多个主键的问题<br>
使用ColNameUtil.pn静态方法，获取实体类中读取方法对应的列名称<br>
<br>
在xml中编写resultmap是件头痛的事，特别是表连接时返回的对象是多样的，如果不按照map返回，分别建resultmap工作量会翻倍。<br>
使用@AutoMap注解entity实体类，就可以在应用启动时解析使用@TableField注解的字段，自动生成scan.mybatis-plus_xxxx为id的resultMap<br>
可以在xml中直接配置使用这个resultMap实例<br>
并且还支持继承关系，扫描实体子类会附加上父类的字段信息一起构建子类的resultmap<br>
对于各种表连接形成的返回实体对象，可以通过继承来生成。通过扫描后自动构建各种resultmap，在xml中引用。<br>
<br>
做连表查询时，输入参数往往不是单一的实体类，而是采用更灵活的Map对象，<br>
但map中key参数的名称定义过于随便，可以使用接口定义常量。但原生mybatis在xml中调用静态类方法和变量时需要填写完整的包名不利于大量采用<br>
是否可以像在mybatisplus中使用lambda表达式翻译entity中的列名称<br>
mpp做了封装支持xml的ognl中引入默认包名，并支持lambda定义列名称<br>
例如xml使用以下语句引入map参数中create_time
原生方式<br>
````
#{create_time}
````
mpp的默认包名引用接口常量方式<br>
配置文件中mpp.utilBasePath可设置ognl默认包名<br>
````
#{${@ColInfo@createTime}}
````
mpp的lambda方式<br>
````
#{${@MPP@col("TestEntity::getCreateTime")}}
````
**从中央库引入jar**
````
    <dependency>
        <groupId>com.github.jeffreyning</groupId>
        <artifactId>mybatisplus-plus</artifactId>
        <version>1.5.1-RELEASE</version>
    </dependency>
````



**在实体类上设置@KeySequence，在插入时对id字段自动填充复杂计算值**
````
@KeySequence("select lpad(max(seqno)+3,10,'0') from test")
@TableName(value = "test")
public class TestEntity {
    @TableId(value = "id", type=IdType.INPUT)
    private Integer id;
````
**1.5.1版本之后需使用@EnableAutoFill注解整体开启自动注入功能**
**在实体类字段上设置@InsertFill @UpdateFill，插入和更新时使用当前时间填充**
````
    @InsertFill("select now()")
    @UpdateFill("select now()")
    @TableField(value="update_time",fill=FieldFill.INSERT_UPDATE)
    private Date updateTime;
````

**在实体类字段上设置@InsertFill，在插入时对seqno字段自动填充复杂计算值**
查询当前最大的seqno值并加3，转换成10位字符串，不够位数时用0填充
````
    @TableField(value="seqno",fill=FieldFill.INSERT )
    @InsertFill("select lpad(max(seqno)+3,10,'0') from test")
    private String seqno;
````

**在启动类中使用@EnableMPP启动扩展自定义填充功能和自动创建resultmap功能**
**在启动类中使用@EnableKeyGen启动主键自定义主键填充功能**
注意如果自己实现了IKeyGenerator会与@EnableKeyGen冲突
````
@SpringBootApplication
@EnableMPP
@EnableKeyGen
public class PlusDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlusDemoApplication.class, args);
    }
}
````

**在实体类上使用@AutoMap注解**
JoinEntity是TestEntity的子类
@TableName(autoResultMap=true)
autoResultMap必须设置为true
父类可以不加@AutoMap，父类设置autoResultMap=true时mybatisplus负责生成resultmap
但原生mybatisplus生成的resultmap的id为mybatis-plus_xxxx没有scan.前缀
````
@AutoMap
@TableName(autoResultMap=true)
public class JoinEntity extends TestEntity{
    @TableField("some2")
    private String some2;

    public String getSome2() {
        return some2;
    }

    public void setSome2(String some2) {
        this.some2 = some2;
    }

    @Override
    public String toString() {
        return "JoinEntity{" +
                "some2='" + some2 + '\'' +
                '}';
    }
}
````

**配置文件中加入扫描entity路径，多个路径用逗号分隔**
````
mpp:
  entityBasePath: com.github.jeffreyning.mybatisplus.demo.entity
````
**配置文件中加入ognl执行java静态方法的类加载默认路径，多个路径用逗号分隔(1.7.0此配置已经删除)**
````
mpp:
  utilBasePath: com.github.jeffreyning.mybatisplus.demo.common
````

**xml文件中引入自动生成的resultMap & xml中使用省略包名调用静态方法（1.7.0省略包名已经删除，使用com.MPP） & @com.MPP@col通过entity的lambda表达式翻译列名**
````
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.github.jeffreyning.mybatisplus.demo.mapper.TestMapper">
    <select id="queryUseRM" resultMap="scan.mybatis-plus_JoinEntity">
        select * from test inner join test2 on test.id=test2.refid
    </select>
    <select id="queryUse" resultMap="scan.mybatis-plus_JoinEntity">
        select * from test inner join test2 on test.id=test2.refid
        where test.create_time <![CDATA[ <= ]]> #{${@com.MPP@col("TestEntity::getCreateTime")}}
        and test.id=#{${@com.MPP@col("TestEntity::getId")}}
    </select>
</mapper>
````

**接口直接返回实例类**
````
@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {
    public List<JoinEntity> queryUseRM();
    public List<JoinEntity> queryUse(Map param);
}
````

**使用ColNameUtil.pn静态方法，获取实体类中读取方法对应的列名称**
````
       System.out.println(ColNameUtil.pn(TestEntity::getCreateTime));
       System.out.println(ColNameUtil.pn(TestEntity::getId));
       System.out.println(ColNameUtil.pn(JoinEntity::getSome2));
       System.out.println(ColNameUtil.pn(JoinEntity::getUpdateTime));
````

**根据多个字段联合主键增删改查**
在实例类成员变量上使用@MppMultiId表明联合主键
````
@TableName("test07")
public class Test07Entity {
    @MppMultiId
    @TableField(value = "k1")
    private Integer k1;

    @MppMultiId
    @TableField(value = "k2")
    private String k2;
    
    @TableField(value = "col1")
    private String col1;
    @TableField(value = "col2")
    private String col2;    
````

mapper需要继承MppBaseMapper
````
@Mapper
public interface Test07Mapper extends MppBaseMapper<Test07Entity> {
}
````
根据多主键增删改查
````
    public void testMultiId(){
        //id
        Test07Entity idEntity=new Test07Entity();
        idEntity.setK1(1);
        idEntity.setK2("111");
        //del
        test07Mapper.deleteByMultiId(idEntity);
        //add
        test07Mapper.insert(idEntity);
        //query
        Test07Entity retEntity=test07Mapper.selectByMultiId(idEntity);
        retEntity.setCol1("xxxx");
        //update
        test07Mapper.updateByMultiId(retEntity);
    }
````
service层继承IMppService接口和MppServiceImpl
````
public interface Test07Service extends IMppService<Test07Entity> {
}
public class Test07ServiceImpl extends MppServiceImpl<Test07Mapper, Test07Entity> implements Test07Service {
}
````

在service层调用多主键操作
````
    public void testMultiIdService(){
        //id
        Test07Entity idEntity=new Test07Entity();
        idEntity.setK1(1);
        idEntity.setK2("111");
        //del

        test07Service.deleteByMultiId(idEntity);
        //add
        test07Service.save(idEntity);
        //query
        Test07Entity retEntity=test07Service.selectByMultiId(idEntity);
        retEntity.setCol1("xxxx");
        //update
        test07Mapper.updateByMultiId(retEntity);
    }
````

根据复合主键进行批量操作和saveOrUpdate操作
````
    @Test
    public void testSaveOrUpdateByMultiIdService(){
        //id
        Test07Entity idEntity=new Test07Entity();
        idEntity.setK1(6);
        idEntity.setK2("666");
        //del
        test07Service.deleteByMultiId(idEntity);
        //add
        test07Service.saveOrUpdateByMultiId(idEntity);
        //update
        idEntity.setCol1("ccccc");
        test07Service.saveOrUpdateByMultiId(idEntity);

    }

    @Test
    public void testSaveOrUpdateBatchByMultiIdService(){
        //ids
        List<Test07Entity> entityList=new ArrayList<Test07Entity>();
        for(int i=10;i<30;i++){
            Test07Entity idEntity=new Test07Entity();
            idEntity.setK1(i);
            idEntity.setK2(String.valueOf(i*10));
            entityList.add(idEntity);
        }

        //del
        for(Test07Entity idEntity:entityList) {
            test07Service.deleteByMultiId(idEntity);
        }
        //add batch
        test07Service.saveOrUpdateBatchByMultiId(entityList);
        //del
        for(Test07Entity idEntity:entityList) {
            idEntity.setCol1(new Date().toString());
        }
        //update batch
        test07Service.saveOrUpdateBatchByMultiId(entityList);

    }

    @Test
    public void testUpdateBatchByMultiIdService(){
        //ids
        List<Test07Entity> entityList=new ArrayList<Test07Entity>();
        for(int i=50;i<80;i++){
            Test07Entity idEntity=new Test07Entity();
            idEntity.setK1(i);
            idEntity.setK2(String.valueOf(i*10));
            entityList.add(idEntity);
        }

        //del
        for(Test07Entity idEntity:entityList) {
            test07Service.deleteByMultiId(idEntity);
        }
        //add batch
        test07Service.saveOrUpdateBatchByMultiId(entityList);
        //del
        for(Test07Entity idEntity:entityList) {
            idEntity.setCol1(new Date().toString());
        }
        //update batch
        test07Service.updateBatchByMultiId(entityList);

    }
````
**优化分页插件实现在不分页时进行排序操作（1.6.0版本已删除）**
使用MppPaginationInterceptor插件
````
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new MppPaginationInterceptor();
    }

````
mapper中按照一般分页接口定义，同时支持返回值为list或page对象的写法
````
@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {
    public List<JoinEntity> queryUseRM(Page page);
}
````
page参数设置size=-1为全量查询，size>0时正常分页，设置OrderItem进行无论是否分页都实现排序
````
    public void testOrder(){
        Page page=new Page();

        page.setSize(-1);
        page.addOrder(new OrderItem().setColumn("test.id").setAsc(true));
        page.addOrder(new OrderItem().setColumn("test2.some2").setAsc(true));

        List rp=testMapper.queryUseRM(page);
    }
````

**常见问题说明**

_报错Caused by: org.apache.ibatis.binding.BindingException: Invalid bound statement (not found):_

解决方法：在启动类中使用@EnableMPP注解，一般报Invalid bound statement (not found)都是没有使用@EnableMPP

_添加@EnableMPP后启动报错required a single bean, but 2 were found_

产生原因:添加@EnableMpp后会启用内置的metaObjectHandler实现类实现自动填充功能，如果旧项目中自行实现了metaObjectHandler会产生required a single bean类似冲突
解决方法:需删掉自定义的继承metaObjectHandler实现的填充功能

_如果加了@EnableMPP后仍然报Invalid bound statement (not found)_

需要检查是否实现了自定义的SqlSessionFactory，如果实现自定义的SqlSessionFactory则需要手工注入
MppSqlInjector（否则引发Invalid bound statement）, MppKeyGenerator(否则无法主键生成), DataAutoFill（否则无法自动填充）
自定义SqlSessionFactory注入示例如下
```    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dateSource, MybatisPlusProperties properties, MppSqlInjector mppSqlInjector, MppKeyGenerator mppKeyGenerator, DataAutoFill dataAutoFill) throws Exception {
        MybatisSqlSessionFactoryBean bean=new MybatisSqlSessionFactoryBean();
        GlobalConfig globalConfig = properties.getGlobalConfig();
        globalConfig.setSqlInjector(mppSqlInjector);
        globalConfig.setMetaObjectHandler(dataAutoFill);
        globalConfig.getDbConfig().setKeyGenerator(mppKeyGenerator);
        bean.setDataSource(dateSource);
        bean.setGlobalConfig(globalConfig);
        return bean.getObject();
    }
```

_如何整合pagehelper插件_

mybatisplus本身有分页常见，如果一定要使用pagehelper插件的话，与原生的mybatisplus有冲突
解决方法为：使用以下代码加载pageHelper(版本为5.1.10)
```
    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                configuration.addInterceptor(new com.github.pagehelper.PageInterceptor());
            }
        };
    }
```


**兼容性说明**

mybatisplus-plus1.5.0兼容mybatisplus3.3.1(mybatis3.5.3)到最新版mybatisplus3.4.2(mybatis3.5.6)
mybatisplus-plus1.5.1与最高到mybatisplus3.4.3.1兼容
（mybatisplus-plus1.5.1与mybatisplus3.4.3不兼容，mybatisplus3.4.3自身有bug无法使用，报sun.reflect.generics.reflectiveObjects.TypeVariableImpl cannot be cast to java.lang.Class）
（mybatisplus-plus1.5.1与mybatisplus3.4.3.2不兼容，报org.apache.ibatis.binding.BindingException: Invalid bound statement (not found)）
mybatisplus-plus1.6.0与mybatisplus3.4.3.2+兼容（已经测试到mybatisplus3.4.3.4）
mybatisplus-plus1.7.0兼容jdk11(删除了自定义ognl根路径功能)

**demo下载**
mybatisplus-plus 1.7.0 示例工程下载地址
https://pan.baidu.com/s/1jI5X0xDDUT4L6gl6QijFHA

mybatisplus-plus 1.6.0 示例工程下载地址
https://pan.baidu.com/s/1ZnLBkl27dr6KVg8D_Pn0Jw

mybatisplus-plus 1.5.1 示例工程下载地址
链接：https://pan.baidu.com/s/1XfRy1jrTyOefp3bqiAmNwg

mybatisplus-plus 1.5.0 示例工程下载地址
链接：https://pan.baidu.com/s/1spa53ShHyXJendr4pMAKsQ 

扫码（或搜索"爱好与编程"）订阅公众号，回复"plus"获取下载密码
![Image text](http://www.jrnsoft.com/qrcode_for_gh.jpg)

