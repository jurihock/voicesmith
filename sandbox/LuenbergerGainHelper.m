A = [ 1 1; 0 1 ];
C = [ 1 0 ];

P = [ 0.8 0.9 ];

K = acker(A',C',P);

disp(sprintf([num2str(P) ' \t => \t ' num2str(K)]));

