-- Fake data for development environment (H2 database only)

-- Insert fake profiles
INSERT INTO profile (id, email, forename, surname, password, job_title, location, avatar, email_verification, last_login, status, subscription) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'john.doe@example.com', 'John', 'Doe', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfp5cyeOOBYrB/wFjKOgDK8a', 'Software Engineer', 'San Francisco, CA', null, true, '2024-09-01 10:30:00', 'ACTIVE', 'PREMIUM'),
('550e8400-e29b-41d4-a716-446655440002', 'jane.smith@example.com', 'Jane', 'Smith', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfp5cyeOOBYrB/wFjKOgDK8a', 'Frontend Developer', 'New York, NY', null, true, '2024-09-02 14:15:00', 'ACTIVE', 'BASIC'),
('550e8400-e29b-41d4-a716-446655440003', 'mike.wilson@example.com', 'Mike', 'Wilson', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfp5cyeOOBYrB/wFjKOgDK8a', 'DevOps Engineer', 'Austin, TX', null, false, '2024-08-28 09:45:00', 'PENDING_VERIFICATION', 'BASIC'),
('550e8400-e29b-41d4-a716-446655440004', 'sarah.johnson@example.com', 'Sarah', 'Johnson', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfp5cyeOOBYrB/wFjKOgDK8a', 'Product Manager', 'Seattle, WA', null, true, '2024-09-03 16:20:00', 'ACTIVE', 'PREMIUM');

-- Insert fake companies
INSERT INTO company (id, name, industry, website, company_size, location, founded_year, description, note, logo, status) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'TechCorp Solutions', 'Technology', 'https://techcorp.com', '501-1000', 'San Francisco, CA', '2010', 'A leading technology company focused on innovative software solutions for enterprise clients.', 'Great company culture and benefits', null, 'INTERESTED'),
('660e8400-e29b-41d4-a716-446655440002', 'DataFlow Inc', 'Data Analytics', 'https://dataflow.com', '51-200', 'New York, NY', '2018', 'Specializes in big data analytics and machine learning solutions for financial services.', 'Fast-growing startup with excellent growth opportunities', null, 'INTERESTED'),
('660e8400-e29b-41d4-a716-446655440003', 'CloudFirst Technologies', 'Cloud Computing', 'https://cloudfirst.com', '201-500', 'Austin, TX', '2015', 'Cloud infrastructure and DevOps automation platform provider.', 'Remote-friendly company', null, 'NOT_INTERESTED'),
('660e8400-e29b-41d4-a716-446655440004', 'InnovateLab', 'Software Development', 'https://innovatelab.com', '11-50', 'Seattle, WA', '2020', 'A startup focused on AI-powered development tools and productivity software.', 'Exciting projects with cutting-edge technology', null, 'INTERESTED'),
('660e8400-e29b-41d4-a716-446655440005', 'MegaCorp Enterprise', 'Enterprise Software', 'https://megacorp.com', '1000+', 'Chicago, IL', '1995', 'Large enterprise software company with global presence.', 'Stable but slow-moving corporate environment', null, 'BLOCKED');

-- Insert fake company meta data
INSERT INTO company_meta_data (company_id, meta_name, meta_value) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'core_technologies', 'Java, Spring Boot, Kubernetes'),
('660e8400-e29b-41d4-a716-446655440001', 'remote_policy', 'Hybrid (3 days remote/week)'),
('660e8400-e29b-41d4-a716-446655440001', 'linkedin', 'https://linkedin.com/company/techcorp-solutions'),
('660e8400-e29b-41d4-a716-446655440002', 'core_technologies', 'Python, Spark, TensorFlow'),
('660e8400-e29b-41d4-a716-446655440002', 'funding_stage', 'Series B'),
('660e8400-e29b-41d4-a716-446655440002', 'remote_policy', 'Remote-first'),
('660e8400-e29b-41d4-a716-446655440003', 'cloud_providers', 'AWS, GCP'),
('660e8400-e29b-41d4-a716-446655440003', 'certifications', 'AWS Advanced Consulting Partner'),
('660e8400-e29b-41d4-a716-446655440004', 'recent_awards', 'Best Startup 2023'),
('660e8400-e29b-41d4-a716-446655440004', 'core_technologies', 'Rust, ML Ops'),
('660e8400-e29b-41d4-a716-446655440005', 'global_offices', 'Chicago; London; Bangalore'),
('660e8400-e29b-41d4-a716-446655440005', 'enterprise_customers', 'BankCorp; InsureCo');

-- Insert fake applications
INSERT INTO application (id, url, title, location, applied_at, salary, note, raw_content, description, cover_letter, resume_insights, status, company_id, profile_id) VALUES
('770e8400-e29b-41d4-a716-446655440001', 'https://techcorp.com/careers/senior-dev', 'Senior Software Engineer', 'San Francisco, CA', '2024-08-15 09:00:00', '$120,000 - $150,000', 'Great opportunity for backend development', 'Senior Software Engineer position...', 'Looking for an experienced backend developer with Java/Spring expertise', 'Dear Hiring Manager, I am excited to apply...', 'Strong match for Java and Spring Boot experience', 'APPLIED', '660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001'),
('770e8400-e29b-41d4-a716-446655440002', 'https://dataflow.com/jobs/frontend-dev', 'Frontend Developer', 'New York, NY', '2024-08-20 11:30:00', '$90,000 - $110,000', 'React and Vue.js focus', 'Frontend Developer role...', 'Seeking a frontend developer with modern JavaScript frameworks', 'I am passionate about creating user-friendly interfaces...', 'Good fit for React experience, some Vue.js knowledge', 'IN_PROGRESS', '660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002'),
('770e8400-e29b-41d4-a716-446655440003', 'https://cloudfirst.com/careers/devops', 'DevOps Engineer', 'Austin, TX', '2024-08-25 14:15:00', '$100,000 - $130,000', 'AWS and Kubernetes required', 'DevOps Engineer position...', 'Looking for DevOps engineer with cloud infrastructure experience', 'My experience with cloud platforms aligns perfectly...', 'Excellent match for AWS and containerization skills', 'OFFER_RECEIVED', '660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003'),
('770e8400-e29b-41d4-a716-446655440004', 'https://innovatelab.com/join/product-manager', 'Product Manager', 'Seattle, WA', '2024-09-01 10:00:00', '$110,000 - $140,000', 'AI product focus', 'Product Manager role...', 'Product Manager for AI-powered development tools', 'With my background in product management and AI...', 'Strong product management background', 'APPLIED', '660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004'),
('770e8400-e29b-41d4-a716-446655440005', 'https://techcorp.com/careers/fullstack', 'Full Stack Developer', 'San Francisco, CA', '2024-08-10 13:45:00', '$95,000 - $125,000', 'Junior to mid-level position', 'Full Stack Developer position...', 'Full stack developer with both frontend and backend skills', 'I bring experience in both frontend and backend...', 'Balanced full-stack experience', 'REJECTED', '660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002'),
('770e8400-e29b-41d4-a716-446655440006', 'https://megacorp.com/careers/architect', 'Software Architect', 'Chicago, IL', '2024-07-30 16:20:00', '$160,000 - $200,000', 'Enterprise architecture role', 'Software Architect position...', 'Senior architect role for enterprise solutions', 'My architectural experience with enterprise systems...', 'Strong enterprise architecture background', 'DECLINED', '660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001');

-- Insert fake application meta data
INSERT INTO application_meta_data (application_id, meta_name, meta_value) VALUES
('770e8400-e29b-41d4-a716-446655440001', 'recruiter', 'Alice Reynolds'),
('770e8400-e29b-41d4-a716-446655440001', 'referral', 'Employee Referral: Jane Smith'),
('770e8400-e29b-41d4-a716-446655440002', 'tech_stack', 'React, TypeScript'),
('770e8400-e29b-41d4-a716-446655440002', 'hiring_manager', 'Carlos Diaz'),
('770e8400-e29b-41d4-a716-446655440003', 'interview_location', 'Remote'),
('770e8400-e29b-41d4-a716-446655440003', 'required_certifications', 'AWS Certified Solutions Architect'),
('770e8400-e29b-41d4-a716-446655440004', 'product_area', 'AI tooling'),
('770e8400-e29b-41d4-a716-446655440004', 'team_size', '5-10'),
('770e8400-e29b-41d4-a716-446655440005', 'level', 'Junior-Mid'),
('770e8400-e29b-41d4-a716-446655440005', 'application_source', 'Company Careers Page'),
('770e8400-e29b-41d4-a716-446655440006', 'sponsorship', 'No'),
('770e8400-e29b-41d4-a716-446655440006', 'remote_possible', 'Yes');

-- Insert fake application attachments
INSERT INTO application_attachment (id, name, path, content_type, content_length, application_id) VALUES
('880e8400-e29b-41d4-a716-446655440001', 'resume.pdf', '/uploads/attachments/resume.pdf', 'application/pdf', 204800, '770e8400-e29b-41d4-a716-446655440001'),
('880e8400-e29b-41d4-a716-446655440002', 'cover_letter.pdf', '/uploads/attachments/cover_letter.pdf', 'application/pdf', 102400, '770e8400-e29b-41d4-a716-446655440002'),
('880e8400-e29b-41d4-a716-446655440003', 'portfolio.zip', '/uploads/attachments/portfolio.zip', 'application/zip', 51200, '770e8400-e29b-41d4-a716-446655440003'),
('880e8400-e29b-41d4-a716-446655440004', 'references.pdf', '/uploads/attachments/references.pdf', 'application/pdf', 30720, '770e8400-e29b-41d4-a716-446655440004'),
('880e8400-e29b-41d4-a716-446655440005', 'git_links.txt', '/uploads/attachments/git_links.txt', 'text/plain', 1024, '770e8400-e29b-41d4-a716-446655440001'),
('880e8400-e29b-41d4-a716-446655440006', 'case_study_dataflow.pdf', '/uploads/attachments/case_study_dataflow.pdf', 'application/pdf', 45200, '770e8400-e29b-41d4-a716-446655440002'),
('880e8400-e29b-41d4-a716-446655440007', 'k8s_manifests.yml', '/uploads/attachments/k8s_manifests.yml', 'text/yaml', 4096, '770e8400-e29b-41d4-a716-446655440003'),
('880e8400-e29b-41d4-a716-446655440008', 'pm_roadmap.pptx', '/uploads/attachments/pm_roadmap.pptx', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 120000, '770e8400-e29b-41d4-a716-446655440004'),
('880e8400-e29b-41d4-a716-446655440009', 'coverletter_fullstack.docx', '/uploads/attachments/coverletter_fullstack.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 16000, '770e8400-e29b-41d4-a716-446655440005'),
('880e8400-e29b-41d4-a716-44665544000a', 'salary_expectations.pdf', '/uploads/attachments/salary_expectations.pdf', 'application/pdf', 2048, '770e8400-e29b-41d4-a716-446655440006');

-- Insert fake application resumes (one per application)
INSERT INTO application_resume (id, name, path, content_type, content_length, application_id) VALUES
('990e8400-e29b-41d4-a716-446655440001', 'resume_techcorp.pdf', '/uploads/resumes/resume_techcorp.pdf', 'application/pdf', 52345, '770e8400-e29b-41d4-a716-446655440001'),
('990e8400-e29b-41d4-a716-446655440002', 'resume_dataflow.pdf', '/uploads/resumes/resume_dataflow.pdf', 'application/pdf', 61234, '770e8400-e29b-41d4-a716-446655440002'),
('990e8400-e29b-41d4-a716-446655440003', 'resume_cloudfirst.pdf', '/uploads/resumes/resume_cloudfirst.pdf', 'application/pdf', 48900, '770e8400-e29b-41d4-a716-446655440003'),
('990e8400-e29b-41d4-a716-446655440004', 'resume_innovatelab.pdf', '/uploads/resumes/resume_innovatelab.pdf', 'application/pdf', 70012, '770e8400-e29b-41d4-a716-446655440004'),
('990e8400-e29b-41d4-a716-446655440005', 'resume_fullstack.pdf', '/uploads/resumes/resume_fullstack.pdf', 'application/pdf', 43000, '770e8400-e29b-41d4-a716-446655440005'),
('990e8400-e29b-41d4-a716-446655440006', 'resume_megacorp.pdf', '/uploads/resumes/resume_megacorp.pdf', 'application/pdf', 98000, '770e8400-e29b-41d4-a716-446655440006');

-- Insert fake application timeline events (4-5 per application)
INSERT INTO application_timeline (id, title, description, status, occurred_at, application_id) VALUES
('a10e8400-e29b-41d4-a716-446655440001', 'Created draft', 'Initial application draft created', 'INITIATED', '2024-08-10 08:30:00', '770e8400-e29b-41d4-a716-446655440001'),
('a10e8400-e29b-41d4-a716-446655440002', 'Submitted online', 'Application submitted via company careers page', 'APPLIED', '2024-08-15 09:00:00', '770e8400-e29b-41d4-a716-446655440001'),
('a10e8400-e29b-41d4-a716-446655440003', 'Phone screen scheduled', '30-minute phone screen scheduled with recruiter', 'IN_PROGRESS', '2024-08-20 11:00:00', '770e8400-e29b-41d4-a716-446655440001'),
('a10e8400-e29b-41d4-a716-446655440004', 'Coding challenge assigned', 'Take-home coding challenge sent to candidate', 'IN_PROGRESS', '2024-08-25 10:00:00', '770e8400-e29b-41d4-a716-446655440001'),
('a10e8400-e29b-41d4-a716-446655440005', 'Created draft', 'Initial application draft created', 'INITIATED', '2024-08-18 09:15:00', '770e8400-e29b-41d4-a716-446655440002'),
('a10e8400-e29b-41d4-a716-446655440006', 'Submitted', 'Applied through referral link', 'APPLIED', '2024-08-20 11:30:00', '770e8400-e29b-41d4-a716-446655440002'),
('a10e8400-e29b-41d4-a716-446655440007', 'Phone interview', 'Phone interview with hiring manager', 'IN_PROGRESS', '2024-08-24 15:00:00', '770e8400-e29b-41d4-a716-446655440002'),
('a10e8400-e29b-41d4-a716-446655440008', 'Onsite interview', 'Onsite/virtual panel interview scheduled', 'IN_PROGRESS', '2024-08-30 09:00:00', '770e8400-e29b-41d4-a716-446655440002'),
('a10e8400-e29b-41d4-a716-446655440009', 'Created draft', 'Initial application created', 'INITIATED', '2024-08-20 10:00:00', '770e8400-e29b-41d4-a716-446655440003'),
('a10e8400-e29b-41d4-a716-44665544000a', 'Submitted', 'Applied via company portal', 'APPLIED', '2024-08-25 14:15:00', '770e8400-e29b-41d4-a716-446655440003'),
('a10e8400-e29b-41d4-a716-44665544000b', 'Technical screen', 'Technical screening completed (good)', 'IN_PROGRESS', '2024-08-28 13:30:00', '770e8400-e29b-41d4-a716-446655440003'),
('a10e8400-e29b-41d4-a716-44665544000c', 'Offer extended', 'Company extended an offer', 'OFFER_RECEIVED', '2024-09-02 10:00:00', '770e8400-e29b-41d4-a716-446655440003'),
('a10e8400-e29b-41d4-a716-44665544000d', 'Created draft', 'Application started', 'INITIATED', '2024-08-28 12:00:00', '770e8400-e29b-41d4-a716-446655440004'),
('a10e8400-e29b-41d4-a716-44665544000e', 'Submitted', 'Submitted application for Product Manager role', 'APPLIED', '2024-09-01 10:00:00', '770e8400-e29b-41d4-a716-446655440004'),
('a10e8400-e29b-41d4-a716-44665544000f', 'Take-home exercise', 'Completed take-home assignment', 'IN_PROGRESS', '2024-09-05 08:00:00', '770e8400-e29b-41d4-a716-446655440004'),
('a10e8400-e29b-41d4-a716-446655440010', 'Panel interview scheduled', 'Panel interview scheduled with PM and engineers', 'IN_PROGRESS', '2024-09-10 14:00:00', '770e8400-e29b-41d4-a716-446655440004'),
('a10e8400-e29b-41d4-a716-446655440011', 'Created draft', 'Application created', 'INITIATED', '2024-08-05 14:00:00', '770e8400-e29b-41d4-a716-446655440005'),
('a10e8400-e29b-41d4-a716-446655440012', 'Submitted', 'Submitted candidate profile and materials', 'APPLIED', '2024-08-10 13:45:00', '770e8400-e29b-41d4-a716-446655440005'),
('a10e8400-e29b-41d4-a716-446655440013', 'Code review', 'Code samples and review completed', 'IN_PROGRESS', '2024-08-16 10:30:00', '770e8400-e29b-41d4-a716-446655440005'),
('a10e8400-e29b-41d4-a716-446655440014', 'Rejected', 'Application was rejected after review', 'REJECTED', '2024-09-05 09:15:00', '770e8400-e29b-41d4-a716-446655440005'),
('a10e8400-e29b-41d4-a716-446655440015', 'Created draft', 'Initial application entry', 'INITIATED', '2024-07-25 11:00:00', '770e8400-e29b-41d4-a716-446655440006'),
('a10e8400-e29b-41d4-a716-446655440016', 'Submitted', 'Applied via company job posting', 'APPLIED', '2024-07-30 16:20:00', '770e8400-e29b-41d4-a716-446655440006'),
('a10e8400-e29b-41d4-a716-446655440017', 'Interview', 'Technical interview completed', 'IN_PROGRESS', '2024-08-10 09:00:00', '770e8400-e29b-41d4-a716-446655440006'),
('a10e8400-e29b-41d4-a716-446655440018', 'Offer received', 'Received offer but candidate declined', 'OFFER_RECEIVED', '2024-08-20 12:00:00', '770e8400-e29b-41d4-a716-446655440006');

-- Insert fake interview notes for applications (2 per application)
INSERT INTO interview_note (id, date, notes, application_id) VALUES
('b10e8400-e29b-41d4-a716-446655440001', '2024-08-20 11:15:00', 'Phone screen with recruiter — good cultural fit, follow-up technical challenge assigned.', '770e8400-e29b-41d4-a716-446655440001'),
('b10e8400-e29b-41d4-a716-446655440002', '2024-08-27 09:45:00', 'Code review session — candidate passed coding challenge with minor issues.', '770e8400-e29b-41d4-a716-446655440001'),
('b10e8400-e29b-41d4-a716-446655440003', '2024-08-21 12:00:00', 'Initial recruiter call via referral — positive, moved to phone interview.', '770e8400-e29b-41d4-a716-446655440002'),
('b10e8400-e29b-41d4-a716-446655440004', '2024-08-30 09:30:00', 'Phone interview with hiring manager — discussed frontend architecture.', '770e8400-e29b-41d4-a716-446655440002'),
('b10e8400-e29b-41d4-a716-446655440005', '2024-08-26 14:00:00', 'Phone technical screen — strong AWS/Kubernetes experience demonstrated.', '770e8400-e29b-41d4-a716-446655440003'),
('b10e8400-e29b-41d4-a716-446655440006', '2024-09-02 10:05:00', 'On-site panel — architecture discussion; offer prepared.', '770e8400-e29b-41d4-a716-446655440003'),
('b10e8400-e29b-41d4-a716-446655440007', '2024-09-03 11:00:00', 'Recruiter screen for PM role — strong product instincts.', '770e8400-e29b-41d4-a716-446655440004'),
('b10e8400-e29b-41d4-a716-446655440008', '2024-09-10 14:30:00', 'Panel interview with engineering and design — follow-up requested.', '770e8400-e29b-41d4-a716-446655440004'),
('b10e8400-e29b-41d4-a716-446655440009', '2024-08-11 09:00:00', 'Submitted materials reviewed — code samples looked good, moved to review.', '770e8400-e29b-41d4-a716-446655440005'),
('b10e8400-e29b-41d4-a716-44665544000a', '2024-09-05 09:10:00', 'Final feedback: rejected due to mismatch on seniority.', '770e8400-e29b-41d4-a716-446655440005'),
('b10e8400-e29b-41d4-a716-44665544000b', '2024-07-31 16:40:00', 'Recruiter call and benefits discussion.', '770e8400-e29b-41d4-a716-446655440006'),
('b10e8400-e29b-41d4-a716-44665544000c', '2024-08-10 09:30:00', 'Technical interview notes: strong system design, candidate later declined offer.', '770e8400-e29b-41d4-a716-446655440006');

-- Insert fake interview attachments (0-2 per interview note)
INSERT INTO interview_attachment (id, name, path, content_type, content_length, interview_note_id) VALUES
('c10e8400-e29b-41d4-a716-446655440001', 'phone_screen_notes_techcorp.txt', '/uploads/interviews/phone_screen_notes_techcorp.txt', 'text/plain', 2048, 'b10e8400-e29b-41d4-a716-446655440001'),
('c10e8400-e29b-41d4-a716-446655440002', 'coding_challenge_solution.zip', '/uploads/interviews/coding_challenge_solution.zip', 'application/zip', 45678, 'b10e8400-e29b-41d4-a716-446655440002'),
('c10e8400-e29b-41d4-a716-446655440003', 'challenge_feedback.pdf', '/uploads/interviews/challenge_feedback.pdf', 'application/pdf', 10240, 'b10e8400-e29b-41d4-a716-446655440002'),
('c10e8400-e29b-41d4-a716-446655440004', 'referral_screencap.png', '/uploads/interviews/referral_screencap.png', 'image/png', 34200, 'b10e8400-e29b-41d4-a716-446655440003'),
('c10e8400-e29b-41d4-a716-446655440005', 'hiring_manager_notes.pdf', '/uploads/interviews/hiring_manager_notes.pdf', 'application/pdf', 18432, 'b10e8400-e29b-41d4-a716-446655440004'),
('c10e8400-e29b-41d4-a716-446655440006', 'k8s_runbook.md', '/uploads/interviews/k8s_runbook.md', 'text/markdown', 5120, 'b10e8400-e29b-41d4-a716-446655440005'),
('c10e8400-e29b-41d4-a716-446655440007', 'offer_terms.pdf', '/uploads/interviews/offer_terms.pdf', 'application/pdf', 22000, 'b10e8400-e29b-41d4-a716-446655440006'),
('c10e8400-e29b-41d4-a716-446655440008', 'pm_screen_notes.txt', '/uploads/interviews/pm_screen_notes.txt', 'text/plain', 4096, 'b10e8400-e29b-41d4-a716-446655440007'),
('c10e8400-e29b-41d4-a716-446655440009', 'panel_followup_tasks.docx', '/uploads/interviews/panel_followup_tasks.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 15360, 'b10e8400-e29b-41d4-a716-446655440008'),
('c10e8400-e29b-41d4-a716-44665544000a', 'code_review_comments.txt', '/uploads/interviews/code_review_comments.txt', 'text/plain', 3072, 'b10e8400-e29b-41d4-a716-446655440009'),
('c10e8400-e29b-41d4-a716-44665544000b', 'rejection_reason.pdf', '/uploads/interviews/rejection_reason.pdf', 'application/pdf', 5120, 'b10e8400-e29b-41d4-a716-44665544000a'),
('c10e8400-e29b-41d4-a716-44665544000c', 'benefits_summary.pdf', '/uploads/interviews/benefits_summary.pdf', 'application/pdf', 9216, 'b10e8400-e29b-41d4-a716-44665544000b'),
('c10e8400-e29b-41d4-a716-44665544000d', 'system_design_notes.pdf', '/uploads/interviews/system_design_notes.pdf', 'application/pdf', 20480, 'b10e8400-e29b-41d4-a716-44665544000c');

-- Insert fake profile resumes (one per profile)
INSERT INTO profile_resume (id, name, path, content_type, content_length, profile_id) VALUES
('a90e8400-e29b-41d4-a716-446655440001', 'john_doe_resume.pdf', '/uploads/profile_resumes/john_doe_resume.pdf', 'application/pdf', 45000, '550e8400-e29b-41d4-a716-446655440001'),
('a90e8400-e29b-41d4-a716-446655440002', 'jane_smith_resume.pdf', '/uploads/profile_resumes/jane_smith_resume.pdf', 'application/pdf', 38000, '550e8400-e29b-41d4-a716-446655440002'),
('a90e8400-e29b-41d4-a716-446655440003', 'mike_wilson_resume.pdf', '/uploads/profile_resumes/mike_wilson_resume.pdf', 'application/pdf', 41000, '550e8400-e29b-41d4-a716-446655440003'),
('a90e8400-e29b-41d4-a716-446655440004', 'sarah_johnson_resume.pdf', '/uploads/profile_resumes/sarah_johnson_resume.pdf', 'application/pdf', 47000, '550e8400-e29b-41d4-a716-446655440004');
