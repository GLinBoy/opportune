-- Create Profile table
CREATE TABLE profile (
    id UUID NOT NULL PRIMARY KEY,
    email VARCHAR(255),
    forename VARCHAR(255),
    surname VARCHAR(255),
    password VARCHAR(255),
    job_title VARCHAR(255),
    location VARCHAR(255),
    avatar VARCHAR(255),
    email_verification BOOLEAN,
    last_login TIMESTAMP,
    status VARCHAR(50) CHECK (status IN ('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION')),
    subscription VARCHAR(255),
    CONSTRAINT uk_profile_email UNIQUE (email)
);

-- Create profile_role table for storing user roles
CREATE TABLE profile_role (
    profile_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,
    CONSTRAINT fk_profile_role_profile FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, role)
);

-- Create Company table
CREATE TABLE company (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    industry VARCHAR(255),
    website VARCHAR(255),
    company_size VARCHAR(255),
    location VARCHAR(255),
    founded_year VARCHAR(255),
    description TEXT,
    note TEXT,
    logo VARCHAR(255),
    status VARCHAR(50) CHECK (status IN ('INTERESTED', 'NOT_INTERESTED', 'BLOCKED')),
    profile_id UUID,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255) NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_company_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create Application table
CREATE TABLE application (
    id UUID NOT NULL PRIMARY KEY,
    url VARCHAR(255),
    title VARCHAR(255),
    location VARCHAR(255),
    applied_at TIMESTAMP,
    salary VARCHAR(255),
    note TEXT,
    raw_content TEXT,
    description TEXT,
    cover_letter TEXT,
    resume_insights TEXT,
    status VARCHAR(50) CHECK (status IN ('INITIATED', 'APPLIED', 'IN_PROGRESS', 'REJECTED', 'OFFER_RECEIVED', 'ACCEPTED', 'DECLINED')),
    company_id UUID,
    profile_id UUID,
    created_by VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_by VARCHAR(255) NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_application_company FOREIGN KEY (company_id) REFERENCES company(id),
    CONSTRAINT fk_application_profile FOREIGN KEY (profile_id) REFERENCES profile(id)
);

-- Create ApplicationTimeline table
CREATE TABLE application_timeline (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50) CHECK (status IN ('INITIATED', 'APPLIED', 'IN_PROGRESS', 'REJECTED', 'OFFER_RECEIVED', 'ACCEPTED', 'DECLINED')),
    occurred_at TIMESTAMP,
    application_id UUID,
    CONSTRAINT fk_application_timeline_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create InterviewNote table
CREATE TABLE interview_note (
    id UUID NOT NULL PRIMARY KEY,
    date TIMESTAMP,
    notes TEXT,
    application_id UUID,
    CONSTRAINT fk_interview_note_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create ProfileResume table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE profile_resume (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    profile_id UUID,
    CONSTRAINT fk_profile_resume_profile FOREIGN KEY (profile_id) REFERENCES profile(id),
    CONSTRAINT uk_profile_resume_profile UNIQUE (profile_id)
);

-- Create ApplicationResume table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE application_resume (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    application_id UUID,
    CONSTRAINT fk_application_resume_application FOREIGN KEY (application_id) REFERENCES application(id),
    CONSTRAINT uk_application_resume_application UNIQUE (application_id)
);

-- Create ApplicationAttachment table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE application_attachment (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    application_id UUID,
    CONSTRAINT fk_application_attachment_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create InterviewAttachment table (extends Attachment with TABLE_PER_CLASS inheritance)
CREATE TABLE interview_attachment (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    path VARCHAR(255),
    content_type VARCHAR(255),
    content_length BIGINT,
    interview_note_id UUID,
    CONSTRAINT fk_interview_attachment_interview_note FOREIGN KEY (interview_note_id) REFERENCES interview_note(id)
);

-- Create CompanyMetaData table with composite primary key
CREATE TABLE company_meta_data (
    id UUID NOT NULL PRIMARY KEY,
    company_id UUID NOT NULL,
    meta_name VARCHAR(255) NOT NULL,
    meta_value TEXT,
    CONSTRAINT fk_company_meta_data_company FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Create ApplicationMetaData table with composite primary key
CREATE TABLE application_meta_data (
    id UUID NOT NULL PRIMARY KEY,
    application_id UUID NOT NULL,
    meta_name VARCHAR(255) NOT NULL,
    meta_value TEXT,
    CONSTRAINT fk_application_meta_data_application FOREIGN KEY (application_id) REFERENCES application(id)
);

-- Create indexes for better performance
CREATE INDEX idx_profile_email ON profile(email);
CREATE INDEX idx_profile_status ON profile(status);
CREATE INDEX idx_profile_role_profile_id ON profile_role(profile_id);
CREATE INDEX idx_company_name ON company(name);
CREATE INDEX idx_company_status ON company(status);
CREATE INDEX idx_company_industry ON company(industry);
CREATE INDEX idx_application_status ON application(status);
CREATE INDEX idx_application_company ON application(company_id);
CREATE INDEX idx_application_profile ON application(profile_id);
CREATE INDEX idx_application_applied_at ON application(applied_at);
CREATE INDEX idx_application_timeline_application ON application_timeline(application_id);
CREATE INDEX idx_application_timeline_status ON application_timeline(status);
CREATE INDEX idx_application_timeline_occurred_at ON application_timeline(occurred_at);
CREATE INDEX idx_interview_note_application ON interview_note(application_id);
CREATE INDEX idx_interview_note_date ON interview_note(date);
CREATE INDEX idx_profile_resume_profile ON profile_resume(profile_id);
CREATE INDEX idx_application_resume_application ON application_resume(application_id);
CREATE INDEX idx_application_attachment_application ON application_attachment(application_id);
CREATE INDEX idx_interview_attachment_interview_note ON interview_attachment(interview_note_id);
CREATE INDEX idx_company_meta_data_meta_name ON company_meta_data(meta_name);
CREATE INDEX idx_application_meta_data_meta_name ON application_meta_data(meta_name);
