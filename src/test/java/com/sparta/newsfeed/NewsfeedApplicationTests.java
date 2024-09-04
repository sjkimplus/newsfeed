package com.sparta.newsfeed;

<<<<<<< HEAD
=======
import com.sparta.newsfeed.entity.User;
import com.sparta.newsfeed.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> 68612036de52452668e0bb1a2d4fcd0357822cad
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
