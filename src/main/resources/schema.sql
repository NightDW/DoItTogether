CREATE TABLE IF NOT EXISTS t_user(
  id           INT            PRIMARY KEY AUTO_INCREMENT,
  username     VARCHAR(255)   NOT NULL UNIQUE,
  password     VARCHAR(255)   NOT NULL,
  introduction VARCHAR(255),
  icon_url     VARCHAR(255),
  email        VARCHAR(255)   NOT NULL UNIQUE,
  phone        VARCHAR(255)   NOT NULL UNIQUE,
  gender       TINYINT(1)     NOT NULL,
  role         VARCHAR(255)   NOT NULL,
  is_valid     TINYINT(1)     NOT NULL,
  -- 验证码在将注册信息存入数据库时生成
  verify_code  VARCHAR(255)   NOT NULL
);

CREATE TABLE IF NOT EXISTS t_group(
  id           INT            PRIMARY KEY AUTO_INCREMENT,
  name		     VARCHAR(255)   NOT NULL,
  slogan       VARCHAR(255),
  icon_url     VARCHAR(255)
);

-- -- 注意，当某个父任务有多个子任务时，子任务的id大小将成为排列子任务顺序的依据，因此需要按顺序插入
-- CREATE TABLE IF NOT EXISTS t_task(
--   id           INT            PRIMARY KEY AUTO_INCREMENT,
--   pub_time     VARCHAR(255)   NOT NULL,
--   -- 结束时间可为空，在完成任务后才非空
--   fin_time     VARCHAR(255),
--   title        VARCHAR(255)   NOT NULL,
--   content      VARCHAR(255)   NOT NULL,
--   result       VARCHAR(255),
--   group_id     INT            NOT NULL,
--   acc_id       INT,
--   father_id    INT,
--   has_son      TINYINT(1)     NOT NULL,
--   CONSTRAINT FOREIGN KEY(group_id) REFERENCES t_group(id),
--   CONSTRAINT FOREIGN KEY(acc_id) REFERENCES t_user_group(id),
--   CONSTRAINT FOREIGN KEY(father_id) REFERENCES t_task(id)
-- );

CREATE TABLE IF NOT EXISTS t_message(
  id           INT            PRIMARY KEY AUTO_INCREMENT,
  is_private   TINYINT(1)     NOT NULL,
  acc_id       INT            NOT NULL,
  sender_id    INT            NOT NULL,
  send_time    VARCHAR(255)   NOT NULL,
  content      VARCHAR(255)   NOT NULL
  -- 由于acc_id既可以是userId，也可以是groupId，因此不添加外键约束
  -- 另外，由于send_id既可以是userId，也可以是membershipId，因此也不添加外键约束
  -- CONSTRAINT FOREIGN KEY(sender_id) REFERENCES t_user(id)
);

CREATE TABLE IF NOT EXISTS t_user_group(
  id           INT            PRIMARY KEY AUTO_INCREMENT,
  user_id      INT            NOT NULL,
  group_id     INT            NOT NULL,
  role         VARCHAR(255)   NOT NULL,
  nickname     VARCHAR(255),
  is_mute      TINYINT(1)     NOT NULL,
  is_show      TINYINT(1)     NOT NULL,
  is_top       TINYINT(1)     NOT NULL,
  CONSTRAINT FOREIGN KEY(user_id) REFERENCES t_user(id),
  CONSTRAINT FOREIGN KEY(group_id) REFERENCES t_group(id)
);

CREATE TABLE IF NOT EXISTS  t_friend(
  id           INT            PRIMARY KEY AUTO_INCREMENT,
  host_id      INT            NOT NULL,
  guest_id     INT            NOT NULL,
  remarks      VARCHAR(255),
  is_show      TINYINT(1)     NOT NULL,
  is_top       TINYINT(1)     NOT NULL,
  CONSTRAINT FOREIGN KEY(host_id) REFERENCES t_user(id),
  CONSTRAINT FOREIGN KEY(guest_id) REFERENCES t_user(id)
);

-- 以下代码复制自22-38行，又进行了一点修改（新增acc_user_id字段）
-- 注意，当某个父任务有多个子任务时，子任务的id大小将成为排列子任务顺序的依据，因此需要按顺序插入
CREATE TABLE IF NOT EXISTS t_task(
  id           INT            PRIMARY KEY AUTO_INCREMENT,
  pub_time     VARCHAR(255)   NOT NULL,
  -- 结束时间可为空，在任务被完成后该字段才非空
  fin_time     VARCHAR(255),
  title        VARCHAR(255)   NOT NULL,
  content      VARCHAR(255)   NOT NULL,
  result       VARCHAR(255),
  group_id     INT            NOT NULL,
  acc_id       INT,
  acc_user_id  INT,
  father_id    INT,
  has_son      TINYINT(1)     NOT NULL,
  CONSTRAINT FOREIGN KEY(group_id) REFERENCES t_group(id),
  CONSTRAINT FOREIGN KEY(acc_id) REFERENCES t_user_group(id),
  CONSTRAINT FOREIGN KEY(acc_user_id) REFERENCES t_user(id),
  CONSTRAINT FOREIGN KEY(father_id) REFERENCES t_task(id)
);