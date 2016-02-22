import numpy as np

class Raster:
    def __index__(self, w, h, data, gap_count=-1):
        self.w = w
        self.h = h
        self.size = w * h
        self.data = data
        self.gap_count = gap_count;

    def is_singular(self):
        return self.w == 1 and self.h == 1

    def is_free_of_gaps(self):
        return self.gap_count == 0

    def is_full_of_gaps(self):
        return self.gap_count == self.size

    def clone(self):
        return Raster(self.w, self.h, np.copy(self.data), self.gap_count)