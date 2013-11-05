BEGIN;

    DELETE FROM used_vehicle;

    INSERT INTO used_vehicle
    SELECT 11, 77, 'text one'
    WHERE NOT EXISTS (
        SELECT id FROM used_vehicle WHERE id = 11
    );

    INSERT INTO used_vehicle
    SELECT 22, 88, 'text two'
    WHERE NOT EXISTS (
        SELECT id FROM used_vehicle WHERE id = 22
    );

    ALTER SEQUENCE used_vehicle_id_seq RESTART WITH 1000;

END;
