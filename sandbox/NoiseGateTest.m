close all;
clear all;
clc;

r = [0:0.0001:1];
coeffs = [1e-10 1e-5 1e-4 1e-3 1e-2 1e-1];
n = length(coeffs);

for i = 1:n
    
    c = coeffs(i);

    % Noise gate 1
    f1 = @(r, c) r ./ (r + c);
    r1 = f1(r, c);

    % Noise gate 2
    f2 = @(r, c) r ./ max(r,c); % (r.^2) ./ (r + c);
    r2 = f2(r, c);
    
    % Calc results
    
    r2db = @(r) 20*log10(r);

    rdb = r2db(r);
    rdb1 = r2db(r1);
    rdb2 = r2db(r2);
    
    % Plot results I
    
    subplot(3,2,i);
    plot(rdb, rdb, 'k--', rdb, rdb1, 'r', rdb, rdb2, 'b');
    legend('r', 'r/(r+c)', 'r^2/(r+c)', 'Location', 'SouthEast');
    xlabel('Input dB');
    ylabel('Output dB');
    title(['c = ' num2str(c)]);
    
    % Plot results II
    
%     rdb1 = (rdb1 - max(rdb1));
%     rdb2 = (rdb2 - max(rdb2));

%     subplot(3,2,i);
%     plot(rdb, rdb1, 'r', rdb, rdb2, 'b');
%     legend('r/(r+c)', 'r^2/(r+c)', 'Location', 'SouthEast');
%     xlabel('Input dB');
%     ylabel('\Delta Output dB');
%     title(['c = ' num2str(c)]);

end