#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <math.h>


double* arange(int x1, int x2) {
   int n = x2 - x1 + 1;
   double* a = malloc(n * sizeof(double));;
   int i;
   for (i = 0; i < n; i++) {
     a[i] = i + x1;
   }
   return a;
}

void ex1(int s, double* a, double* b) {
  double* c = malloc(s * sizeof(double));;
  int i;
  for (i = 0; i < s; i++) {
    c[i] = sin(2.2 * a[i] - 3.3 * b[i]) / sqrt(4.4 * a[i] + 5.5 * b[i]);
  }
  free(c);
}

int main(int argc, char **argv) {
  int times = 100;
  int s = 2;
  printf("%s\t%s\t%s\n", "No", "Size", "C");
  int k;
  for (k = 0; k < 20; k++) {
    double* a = arange(1, s);
    double* b = arange(1, s);
    
    clock_t t = clock();    
    int i;
    for (i = 0; i < times; i++) {
      ex1(s, a, b);      
    }
    t = clock() - t;

    free(b);
    free(a);

    printf("%d\t%d\t%f\n", k + 1, s, ((float)t)/CLOCKS_PER_SEC);
    s *= 2;
  }
}