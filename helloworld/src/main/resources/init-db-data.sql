BEGIN;

INSERT INTO used_vehicle
SELECT 11, 77
WHERE NOT EXISTS (
    SELECT id FROM used_vehicle WHERE id = 11
);

INSERT INTO used_vehicle
SELECT 22, 88
WHERE NOT EXISTS (
    SELECT id FROM used_vehicle WHERE id = 22
);

END;
