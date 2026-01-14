-- Sample Federation Remote Modules
-- These are default modules available to all tenants

INSERT INTO federation_remotes (id, name, display_name, description, remote_entry_url, exposed_module, icon, enabled, display_order)
VALUES
    (gen_random_uuid(), 'iam', 'IAM Module', 'Identity & Access Management', 'http://localhost:4201/remoteEntry.json', './Component', 'pi-shield', true, 1),
    (gen_random_uuid(), 'crm', 'CRM Module', 'Customer Relationship Management', 'http://localhost:4202/remoteEntry.json', './Component', 'pi-users', true, 2),
    (gen_random_uuid(), 'bpm', 'BPM Module', 'Business Process Management', 'http://localhost:4210/remoteEntry.json', './Component', 'pi-sitemap', true, 3)
ON CONFLICT (name) DO NOTHING;
