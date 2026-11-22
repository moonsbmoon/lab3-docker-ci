CREATE TABLE saga_states (
                             id UUID PRIMARY KEY,
                             enrollment_id BIGINT NOT NULL,
                             current_state VARCHAR(50) NOT NULL,
                             command_id UUID,
                             updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE saga_transitions (
                                  id BIGSERIAL PRIMARY KEY,
                                  saga_id UUID NOT NULL REFERENCES saga_states(id),
                                  from_state VARCHAR(50),
                                  to_state VARCHAR(50),
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);