package com.sparta.newsfeed;

import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NewsfeedApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void loadUser() {
		// Creating a User object with fake data
		User user = new User();
		user.setEmail("test@example.com");
		user.setPassword("password123");
		user.setName("John Doe");
		user.setBirthday("1990-01-01");
		user.setPostQuantity(0L);
		user.setDateDeleted(null); // Optional field, can be null

		// Saving the User object to the database
		userRepository.save(user);
	}

}
