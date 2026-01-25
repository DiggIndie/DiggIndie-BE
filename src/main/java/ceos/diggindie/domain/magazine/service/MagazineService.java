package ceos.diggindie.domain.magazine.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.MagazineSortOrder;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.magazine.dto.MagazineResponse;
import ceos.diggindie.domain.magazine.entity.Magazine;
import ceos.diggindie.domain.magazine.repository.MagazineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MagazineService {

    private final MagazineRepository magazineRepository;

    public MagazineResponse.MagazineListDTO searchMagazines(String query, MagazineSortOrder order, Pageable pageable) {
        Page<Magazine> magazinePage = switch (order) {
            case recent -> magazineRepository.searchMagazinesByRecent(query, pageable);
            case view -> magazineRepository.searchMagazinesByView(query, pageable);
        };

        return MagazineResponse.MagazineListDTO.from(magazinePage);
    }

    @Transactional
    public MagazineResponse.MagazineInfo getMagazineDetail(Long magazineId) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MAGAZINE_NOT_FOUND));

        // 조회수 증가
        magazine.increaseView();

        return MagazineResponse.MagazineInfo.from(magazine);
    }
}

