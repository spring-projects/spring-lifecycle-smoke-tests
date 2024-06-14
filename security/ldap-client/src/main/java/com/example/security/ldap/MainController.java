package com.example.security.ldap;

import java.util.List;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapClient;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.security.ldap.userdetails.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	private final LdapClient ldap;

	public MainController(LdapClient ldap) {
		this.ldap = ldap;
	}

	@GetMapping("/users")
	public List<Person> users() {
		return this.ldap.search()
			.query((query) -> query.where("objectclass").is("person"))
			.toList(new PersonContextMapper());
	}

	private static final class PersonContextMapper extends AbstractContextMapper<Person> {

		@Override
		protected Person doMapFromContext(DirContextOperations ctx) {
			Person.Essence essense = new Person.Essence(ctx);
			essense.setUsername(ctx.getStringAttribute("uid"));
			return (Person) essense.createUserDetails();
		}

	}

}
