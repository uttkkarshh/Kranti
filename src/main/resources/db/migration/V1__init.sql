-- Flyway V1: initial schema for Kranti

CREATE TABLE IF NOT EXISTS UserProfile (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255),
  email VARCHAR(255),
  bio TEXT,
  profilepicture TEXT,
  password VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Event (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  description TEXT,
  start_date DATETIME,
  end_date DATETIME,
  location POINT,
  SPATIAL INDEX(location)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS event_hosts (
  user_id BIGINT NOT NULL,
  event_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, event_id),
  CONSTRAINT fk_event_hosts_user FOREIGN KEY (user_id) REFERENCES UserProfile(id) ON DELETE CASCADE,
  CONSTRAINT fk_event_hosts_event FOREIGN KEY (event_id) REFERENCES Event(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS event_followers (
  user_id BIGINT NOT NULL,
  event_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, event_id),
  CONSTRAINT fk_event_followers_user FOREIGN KEY (user_id) REFERENCES UserProfile(id) ON DELETE CASCADE,
  CONSTRAINT fk_event_followers_event FOREIGN KEY (event_id) REFERENCES Event(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Post (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  author_id BIGINT NOT NULL,
  event_id BIGINT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  likes_count INT DEFAULT 0,
  shares_count INT DEFAULT 0,
  CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES UserProfile(id) ON DELETE CASCADE,
  CONSTRAINT fk_post_event FOREIGN KEY (event_id) REFERENCES Event(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE,
  CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES UserProfile(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Follower (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  follower_id BIGINT,
  following_id BIGINT,
  follow_date DATETIME,
  CONSTRAINT fk_follower_follower FOREIGN KEY (follower_id) REFERENCES UserProfile(id) ON DELETE CASCADE,
  CONSTRAINT fk_follower_following FOREIGN KEY (following_id) REFERENCES UserProfile(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS FollowRequest (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sender_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  requested_at DATETIME,
  status VARCHAR(50) NOT NULL,
  CONSTRAINT fk_followreq_sender FOREIGN KEY (sender_id) REFERENCES UserProfile(id) ON DELETE CASCADE,
  CONSTRAINT fk_followreq_receiver FOREIGN KEY (receiver_id) REFERENCES UserProfile(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `Like` (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE,
  CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES UserProfile(id) ON DELETE CASCADE
) ENGINE=InnoDB;
