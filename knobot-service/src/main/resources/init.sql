create database knobot;

create table chat_conversation
(
    id          bigint auto_increment comment '自增主键'
        primary key,
    memory_id   varchar(255)                       null comment '对话记忆编号',
    user_id     bigint                             null comment '用户编号',
    title       varchar(255)                       null comment '会话的标题，支持Emoji',
    status      int      default 0                 null comment '会话的状态',
    create_time datetime default CURRENT_TIMESTAMP null comment '会话记录的创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '会话记录的更新时间，自动更新'
)
    comment '聊天会话记录表';

create table chat_message
(
    id               bigint auto_increment comment '自增主键'
        primary key,
    message_id       varchar(255)                       not null comment '消息编号',
    memory_id        varchar(255)                       null comment '对话记忆编号',
    role             varchar(255)                       null comment '消息发送者的角色',
    content          text                               null comment '消息的原始内容，支持Emoji',
    enhanced_content text                               null comment '消息的增强内容，支持Emoji',
    tokens           int                                null comment '消息的令牌数量',
    create_time      datetime default CURRENT_TIMESTAMP null comment '消息记录的创建时间'
)
    comment '聊天消息记录表';

create table knowledge_lib
(
    knowledge_lib_id   varchar(50)                         not null comment '知识库编号'
        primary key,
    user_id            varchar(50)                         null comment '用户编号',
    knowledge_lib_name varchar(255)                        not null comment '知识库名称',
    knowledge_lib_desc varchar(255)                        not null comment '知识库描述',
    document_count     int       default 0                 not null comment '文档数量',
    create_time        timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time        timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '知识库表' charset = utf8mb4;

create table knowledge_lib_document
(
    document_id      varchar(50)                         not null comment '文档编号',
    knowledge_lib_id varchar(50)                         not null comment '关联知识库编号',
    document_name    varchar(100)                        null,
    document_desc    varchar(255)                        null comment '文档描述',
    document_size    double(8, 2)                        not null comment '文档大小',
    url              varchar(200)                        null comment '文档存储url',
    path             varchar(200)                        null comment '文档存储本地路径',
    upload_time      timestamp default CURRENT_TIMESTAMP not null comment '上传时间',
    primary key (knowledge_lib_id, document_id),
    constraint knowledge_lib_document_ibfk_1
        foreign key (knowledge_lib_id) references knowledge_lib (knowledge_lib_id)
)
    comment '知识库文档表' charset = utf8mb4;

create table user_info
(
    user_id     bigint                              not null
        primary key,
    username    varchar(50)                         not null,
    password    varchar(255)                        not null,
    email       varchar(100)                        null,
    avatar_url  varchar(255)                        null,
    nickname    varchar(50)                         null,
    description varchar(200)                        null,
    create_time timestamp default CURRENT_TIMESTAMP null,
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint email
        unique (email),
    constraint username
        unique (username)
);

