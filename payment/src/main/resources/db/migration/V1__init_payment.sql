CREATE TABLE payment_transactions (
                                      id BIGSERIAL PRIMARY KEY,
                                      command_id UUID NOT NULL,
                                      status VARCHAR(50) NOT NULL,
                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);