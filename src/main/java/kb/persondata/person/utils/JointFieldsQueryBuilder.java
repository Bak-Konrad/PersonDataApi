package kb.persondata.person.utils;

import kb.persondata.person.model.Person;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JointFieldsQueryBuilder {

    public Specification<Person> createSpecification(Map<String, String> parameters) {
        Specification<Person> specification = Specification.where(null);

        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey("entityType")) {
                specification = specification.and((root, query, builder) ->
                        builder.like(builder.lower(root.get("entityType")), "%" + parameters.get("entityType").toLowerCase() + "%"));
            }

            if (parameters.containsKey("firstName")) {
                specification = specification.and((root, query, builder) ->
                        builder.like(builder.lower(root.get("firstName")), "%" + parameters.get("firstName").toLowerCase() + "%"));
            }

            if (parameters.containsKey("lastName")) {
                specification = specification.and((root, query, builder) ->
                        builder.like(builder.lower(root.get("lastName")), "%" + parameters.get("lastName").toLowerCase() + "%"));
            }

            if (parameters.containsKey("personalNumber")) {
                specification = specification.and((root, query, builder) ->
                        builder.equal(root.get("personalNumber"), parameters.get("personalNumber")));
            }

            if (parameters.containsKey("fromHeight") && parameters.containsKey("toHeight")) {
                specification = specification.and((root, query, builder) ->
                        builder.between(root.get("height"),
                                Double.parseDouble(parameters.get("fromHeight")),
                                Double.parseDouble(parameters.get("toHeight"))));
            } else if (parameters.containsKey("height")) {
                specification = specification.and((root, query, builder) ->
                        builder.equal(root.get("height"), Double.parseDouble(parameters.get("height"))));
            }

            if (parameters.containsKey("fromWeight") && parameters.containsKey("toWeight")) {
                specification = specification.and((root, query, builder) ->
                        builder.between(root.get("weight"),
                                Double.parseDouble(parameters.get("fromWeight")),
                                Double.parseDouble(parameters.get("toWeight"))));
            } else if (parameters.containsKey("weight")) {
                specification = specification.and((root, query, builder) ->
                        builder.equal(root.get("weight"), Double.parseDouble(parameters.get("weight"))));
            }

            if (parameters.containsKey("emailAddress")) {
                specification = specification.and((root, query, builder) ->
                        builder.like(builder.lower(root.get("emailAddress")), "%" + parameters.get("emailAddress").toLowerCase() + "%"));
            }
        }
        return specification;
    }
}
