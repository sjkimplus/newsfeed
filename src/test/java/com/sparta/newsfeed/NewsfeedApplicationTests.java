package com.sparta.newsfeed;

import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(readOnly = true)
class NewsfeedApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void loadUser() {
	}

}
