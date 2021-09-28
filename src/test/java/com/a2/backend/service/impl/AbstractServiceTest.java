package com.a2.backend.service.impl;

import com.a2.backend.AbstractTest;
import javax.transaction.Transactional;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ActiveProfiles(profiles = "local")
@ContextConfiguration
abstract class AbstractServiceTest extends AbstractTest {}
